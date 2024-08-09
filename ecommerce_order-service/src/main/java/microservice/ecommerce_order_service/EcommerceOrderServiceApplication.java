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
		"at.backend.drugstore.microservice.common_models.GlobalFacadeService.Inventory",
		"at.backend.drugstore.microservice.common_models.Validations"})
public class EcommerceOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceOrderServiceApplication.class, args);
	}

}