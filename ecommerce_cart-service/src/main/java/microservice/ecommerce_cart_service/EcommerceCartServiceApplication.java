package microservice.ecommerce_cart_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@SpringBootApplication
@EnableWebMvc
@EnableEurekaServer
@ComponentScan(basePackages = {"microservice.ecommerce_cart_service",
		"at.backend.drugstore.microservice.common_models.GlobalFacadeService.Clients" ,
		"at.backend.drugstore.microservice.common_models.GlobalFacadeService.Products",
		"at.backend.drugstore.microservice.common_models.GlobalFacadeService.ESale",
		"at.backend.drugstore.microservice.common_models.Validations",
		"at.backend.drugstore.microservice.common_models.Middleware"})
public class EcommerceCartServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceCartServiceApplication.class, args);
	}

}
