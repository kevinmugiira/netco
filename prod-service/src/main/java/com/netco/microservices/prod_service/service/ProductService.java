package com.netco.microservices.prod_service.service;


import com.netco.microservices.prod_service.dto.ProductRequest;
import com.netco.microservices.prod_service.dto.ProductResponse;
import com.netco.microservices.prod_service.model.Product;
import com.netco.microservices.prod_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@RequiredArgsConstructor
//@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product(
                null,
                productRequest.name(),
                productRequest.description(),
                productRequest.price());
//                .build();
        productRepository.save(product);
        System.out.println("Product created successfully");
//        log.info("Product created successfully!");
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice());

    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice()))
                .toList();
    }
}
