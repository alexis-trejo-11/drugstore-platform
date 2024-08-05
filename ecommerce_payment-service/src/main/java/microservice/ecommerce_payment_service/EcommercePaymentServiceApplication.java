package microservice.ecommerce_payment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@ComponentScan(basePackages = {"microservice.ecommerce_payment_service", "at.backend.drugstore.microservice.common_models.ExternalService.Clients" , "at.backend.drugstore.microservice.common_models.Validations"})

public class EcommercePaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommercePaymentServiceApplication.class, args);
	}

}