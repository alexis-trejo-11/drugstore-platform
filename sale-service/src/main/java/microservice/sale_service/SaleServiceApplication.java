package microservice.sale_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"microservice.sale_service", "at.backend.drugstore.microservice.common_models.ExternalService.Products", "at.backend.drugstore.microservice.common_models.ExternalService.Employee" , "at.backend.drugstore.microservice.common_models.ExternalService.Inventory"})

public class SaleServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaleServiceApplication.class, args);
	}

}