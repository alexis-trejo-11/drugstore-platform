package microservice.client_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableEurekaServer
@ComponentScan(basePackages = {"at.backend.drugstore.microservice.common_classes.GlobalExceptions",
		"microservice.client_service",
		"at.backend.drugstore.microservice.common_classes.Security"})
public class ClientServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientServiceApplication.class, args);
	}

}
