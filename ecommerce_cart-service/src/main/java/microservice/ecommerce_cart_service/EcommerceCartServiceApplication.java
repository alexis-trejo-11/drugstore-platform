package microservice.ecommerce_cart_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableEurekaServer
@ComponentScan(basePackages = {"microservice.ecommerce_cart_service",
		"at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client" ,
		"at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Order",
		"at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Products",
		"at.backend.drugstore.microservice.common_classes.GlobalFacadeService.ESale",
		"at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Payment",
		"at.backend.drugstore.microservice.common_classes.GlobalExceptions",
		"at.backend.drugstore.microservice.common_classes.Middleware"})
public class EcommerceCartServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceCartServiceApplication.class, args);
	}

}
