package microservice.test_service;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaServer
@ComponentScan(basePackages = {"microservice.test_service",
		"at.backend.drugstore.microservice.common_models.GlobalFacadeService.Cart",
		"at.backend.drugstore.microservice.common_models.GlobalFacadeService.Client",
		"at.backend.drugstore.microservice.common_models.GlobalFacadeService.Cart",
		"at.backend.drugstore.microservice.common_models.Validations",
		"at.backend.drugstore.microservice.common_models.GlobalExceptions",
		"at.backend.drugstore.microservice.common_models.Middleware"})
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}