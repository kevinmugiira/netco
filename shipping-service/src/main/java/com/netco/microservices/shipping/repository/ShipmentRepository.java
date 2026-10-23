package com.netco.microservices.shipping.repository;

import com.netco.microservices.shipping.model.ShipmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<ShipmentRecord, String> {}
