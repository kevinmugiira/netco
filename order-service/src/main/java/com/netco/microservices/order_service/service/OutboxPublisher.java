package com.netco.microservices.order_service.service;

import com.netco.microservices.order_service.model.OutboxEvent;
import com.netco.microservices.order_service.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class OutboxPublisher {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper;
    public OutboxPublisher(OutboxEventRepository repository, KafkaTemplate<String, String> kafka, ObjectMapper mapper) {
        this.repository = repository; this.kafka = kafka; this.mapper = mapper;
    }

    @Scheduled(fixedDelayString = "${outbox.publish-delay-ms:1000}")
    @Transactional
    public void publishPending() {
        for (OutboxEvent event : repository.findTop100ByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc("PENDING", Instant.now())) {
            try {
                kafka.send("orders.placed", event.getAggregateId(), event.getPayload()).get(5, TimeUnit.SECONDS);
                event.markSent();
            } catch (Exception e) {
                event.markFailed(e);
                log.warn("Outbox event {} publish failed; retry {} scheduled at {}", event.getId(), event.getAttempts(), event.getNextAttemptAt());
            }
        }
    }
}
