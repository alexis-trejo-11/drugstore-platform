package microservice.client_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableWebMvc
@EnableEurekaServer
@ComponentScan(basePackages = {"at.backend.drugstore.microservice.common_models.Validations",
		"microservice.client_service"})
public class ClientServiceApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ClientServiceApplication.class, args);
	}

}
