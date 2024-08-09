package microservice.user_service;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableEurekaServer
@ComponentScan(basePackages = {"microservice.user_service",
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