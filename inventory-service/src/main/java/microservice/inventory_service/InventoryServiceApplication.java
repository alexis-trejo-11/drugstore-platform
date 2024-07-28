package microservice.inventory_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"microservice.inventory_service", "at.backend.drugstore.microservice.common_models.ExternalService.Products", "at.backend.drugstore.microservice.common_models.ExternalService.Employee", "at.backend.drugstore.microservice.common_models.Validations"})
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

}