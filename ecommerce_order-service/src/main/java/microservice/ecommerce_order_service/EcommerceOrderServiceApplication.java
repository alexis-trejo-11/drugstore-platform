package microservice.ecommerce_order_service;

import io.github.resilience4j.bulkhead.autoconfigure.BulkheadAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication(exclude = {BulkheadAutoConfiguration.class})
@ComponentScan(basePackages = {"microservice.ecommerce_order_service", "at.backend.drugstore.microservice.common_models.ExternalService.Clients", "at.backend.drugstore.microservice.common_models.Validations"})

public class EcommerceOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceOrderServiceApplication.class, args);
	}

}