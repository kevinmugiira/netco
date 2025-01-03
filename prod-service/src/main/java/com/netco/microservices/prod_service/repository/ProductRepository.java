package com.netco.microservices.prod_service.repository;

import com.netco.microservices.prod_service.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
