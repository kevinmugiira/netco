CREATE TABLE IF NOT EXISTS outbox_events (
    id VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload LONGTEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    attempts INT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    next_attempt_at DATETIME(6) NOT NULL,
    last_error VARCHAR(1000) NULL,
    PRIMARY KEY (id),
    INDEX idx_outbox_due (status, next_attempt_at)
) ENGINE=InnoDB;

ALTER TABLE outbox_events MODIFY COLUMN payload LONGTEXT NOT NULL;
