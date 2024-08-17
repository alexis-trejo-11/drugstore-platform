package microservice.ecommerce_sale_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
@ComponentScan(basePackages = { "microservice.ecommerce_sale_service",
		"microservice.ecommerce_common_classes",
		"at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Inventory",
		"at.backend.drugstore.microservice.common_classes.GlobalExceptions"})

public class EcommerceSaleServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(EcommerceSaleServiceApplication.class, args);
	}

}