package microservice.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableEurekaServer
@ComponentScan(basePackages = {"microservice.user_service",
		"at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Cart",
		"at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client",
		"at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Cart",
		"at.backend.drugstore.microservice.common_classes.GlobalExceptions",
		"at.backend.drugstore.microservice.common_classes.Middleware"})
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}