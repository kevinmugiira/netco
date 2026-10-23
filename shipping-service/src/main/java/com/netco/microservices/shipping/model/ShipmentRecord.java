package com.netco.microservices.shipping.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "shipment_records")
public class ShipmentRecord {
    @Id private String orderNumber;
    private String providerShipmentId;
    private Instant createdAt;
    protected ShipmentRecord() {}
    public ShipmentRecord(String orderNumber, String providerShipmentId) {
        this.orderNumber = orderNumber; this.providerShipmentId = providerShipmentId; this.createdAt = Instant.now();
    }
}
