package microservice.ecommerce_payment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
@ComponentScan(basePackages = {"microservice.ecommerce_payment_service",
		"at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client" ,
		"at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Order" ,
		"at.backend.drugstore.microservice.common_classes.GlobalFacadeService.ESale" ,
		"at.backend.drugstore.microservice.common_classes.GlobalExceptions",
		"at.backend.drugstore.microservice.common_classes.Utils",
		"at.backend.drugstore.microservice.common_classes.Security"})

public class EcommercePaymentServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(EcommercePaymentServiceApplication.class, args);
	}

}