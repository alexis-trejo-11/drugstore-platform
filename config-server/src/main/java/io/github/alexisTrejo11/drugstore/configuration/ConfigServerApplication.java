package io.github.alexisTrejo11.drugstore.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

  @PostConstruct
  public void loadEnv() {
    Dotenv dotenv = Dotenv.configure()
        .directory("./")
        .ignoreIfMissing()
        .load();

    dotenv.entries().forEach(entry -> {
      System.setProperty(entry.getKey(), entry.getValue());
      System.out.println("Loaded env: " + entry.getKey() + "=***"); // Mask secrets
    });
  }

  public static void main(String[] args) {
    SpringApplication.run(ConfigServerApplication.class, args);
  }
}
