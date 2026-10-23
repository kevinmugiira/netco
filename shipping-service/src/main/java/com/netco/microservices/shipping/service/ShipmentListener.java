package com.netco.microservices.shipping.service;

import com.netco.microservices.shipping.event.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.netco.microservices.shipping.repository.ShipmentRepository;
import com.netco.microservices.shipping.model.ShipmentRecord;

@Component
public class ShipmentListener {
    private static final Logger log = LoggerFactory.getLogger(ShipmentListener.class);
    private final ShippoClient shippo;
    private final ShipmentRepository repository;
    public ShipmentListener(ShippoClient shippo, ShipmentRepository repository) { this.shippo = shippo; this.repository = repository; }

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 1000, multiplier = 2))
    @KafkaListener(topics = "orders.placed", groupId = "shipping-service")
    @Transactional
    public void createShipment(OrderPlacedEvent event) {
        if (repository.existsById(event.orderNumber())) {
            log.info("Shipment already exists for order {}; ignoring duplicate event", event.orderNumber());
            return;
        }
        String shipmentId = shippo.createTestShipment(event);
        repository.save(new ShipmentRecord(event.orderNumber(), shipmentId));
        log.info("Created Shippo test shipment {} for order {}", shipmentId, event.orderNumber());
    }

    @DltHandler
    public void onDeadLetter(OrderPlacedEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("Shipment for order {} exhausted retries; retained in Kafka topic {}", event.orderNumber(), topic);
    }
}
