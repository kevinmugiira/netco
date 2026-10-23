package com.netco.microservices.order_service.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events", indexes = @Index(name = "idx_outbox_due", columnList = "status,next_attempt_at"))
public class OutboxEvent {
    @Id private String id;
    @Column(nullable = false) private String aggregateId;
    @Column(nullable = false, length = 100) private String eventType;
    @Column(nullable = false, columnDefinition = "LONGTEXT") private String payload;
    @Column(nullable = false, length = 20) private String status;
    @Column(nullable = false) private int attempts;
    @Column(nullable = false) private Instant createdAt;
    @Column(nullable = false) private Instant nextAttemptAt;
    @Column(length = 1000) private String lastError;

    protected OutboxEvent() {}
    public static OutboxEvent pending(String aggregateId, String payload) {
        OutboxEvent e = new OutboxEvent(); e.id = UUID.randomUUID().toString();
        e.aggregateId = aggregateId; e.eventType = "OrderPlaced"; e.payload = payload;
        e.status = "PENDING"; e.createdAt = Instant.now(); e.nextAttemptAt = e.createdAt; return e;
    }
    public String getId() { return id; }
    public String getAggregateId() { return aggregateId; }
    public String getPayload() { return payload; }
    public int getAttempts() { return attempts; }
    public Instant getNextAttemptAt() { return nextAttemptAt; }
    public void markSent() { status = "SENT"; lastError = null; }
    public void markFailed(Throwable error) {
        attempts++; lastError = error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage();
        if (lastError.length() > 1000) lastError = lastError.substring(0, 1000);
        long delay = Math.min(300, 1L << Math.min(attempts, 8));
        nextAttemptAt = Instant.now().plusSeconds(delay);
    }
}
