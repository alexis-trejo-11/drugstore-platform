package at.backend.drugstore.microservice.common_models;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.scheduling.annotation.EnableAsync;



@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
@EnableEurekaServer
public class CommonModelsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommonModelsApplication.class, args);
	}

}
