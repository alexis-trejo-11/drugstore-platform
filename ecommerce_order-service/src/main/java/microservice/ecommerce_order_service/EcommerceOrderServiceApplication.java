package microservice.ecommerce_order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


@SpringBootApplication
@EnableEurekaServer
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"microservice.ecommerce_order_service",
		"at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Inventory",
		"at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client",
		"at.backend.drugstore.microservice.common_classes.GlobalExceptions",
		"at.backend.drugstore.microservice.common_classes.Security"})
public class EcommerceOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceOrderServiceApplication.class, args);
	}

}