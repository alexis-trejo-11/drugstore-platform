package microservice.ecommerce_cart_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"microservice.ecommerce_cart_service", "at.backend.drugstore.microservice.common_models.ExternalService.Products" , "at.backend.drugstore.microservice.common_models.Validations"})
public class EcommerceCartServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceCartServiceApplication.class, args);
	}

}
