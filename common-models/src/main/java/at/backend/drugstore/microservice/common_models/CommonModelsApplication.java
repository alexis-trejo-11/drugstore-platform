package at.backend.drugstore.microservice.common_models;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CommonModelsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommonModelsApplication.class, args);
	}

}
