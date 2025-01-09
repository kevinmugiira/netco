package com.netco.microservices.inventory_service;

import com.netco.microservices.inventory_service.model.Inventory;
import com.netco.microservices.inventory_service.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
//@EnableEurekaClient
public class InventoryServiceApplication {

	public static void main(String[] args) { SpringApplication.run(InventoryServiceApplication.class, args);}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return args -> {
			Inventory inventory = new Inventory();
			inventory.setSkuCode("Alaska");
			inventory.setQuantity(3);

			Inventory inventory1 = new Inventory();
			inventory1.setSkuCode("Terana");
			inventory1.setQuantity(0);

			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory1);

		};
	}


}
