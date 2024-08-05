package microservice.ecommerce_sale_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@ComponentScan(basePackages = {"microservice.ecommerce_sale_service", "at.backend.drugstore.microservice.common_models.ExternalService.Inventory", "at.backend.drugstore.microservice.common_models.Validations"})

public class EcommerceSaleServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceSaleServiceApplication.class, args);
	}

}