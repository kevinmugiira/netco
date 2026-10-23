package com.netco.microservices.notification_service.service;

import com.netco.microservices.notification_service.event.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
import java.util.List;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final WebClient client = WebClient.builder().baseUrl("https://api.resend.com").build();
    @Value("${email.api-key:}") private String apiKey;
    @Value("${email.from:onboarding@resend.dev}") private String from;

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = "orders.placed", groupId = "email-service")
    public void sendOrderConfirmation(OrderPlacedEvent event) {
        if (event.customerEmail() == null || event.customerEmail().isBlank())
            throw new IllegalArgumentException("Order event has no customer email");
        if (apiKey.isBlank()) throw new IllegalStateException("RESEND_API_KEY is required to send order emails");
        Map<String, Object> body = Map.of("from", from, "to", List.of(event.customerEmail()),
                "subject", "Order confirmed: " + event.orderNumber(),
                "html", "<p>Your order <strong>" + event.orderNumber() + "</strong> has been confirmed.</p>");
        client.post().uri("/emails").header("Authorization", "Bearer " + apiKey)
                .header("Idempotency-Key", "order-confirmation-" + event.orderNumber())
                .bodyValue(body).retrieve().toBodilessEntity().block();
        log.info("Order confirmation email sent for order {}", event.orderNumber());
    }

    @DltHandler
    public void onDeadLetter(OrderPlacedEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("Email delivery for order {} exhausted retries; retained in Kafka topic {}", event.orderNumber(), topic);
    }
}
