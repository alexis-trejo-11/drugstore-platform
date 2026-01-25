package microservice.cart_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = { "microservice.cart_service", "libs_kernel" })
public class CartServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(CartServiceApplication.class, args);
  }

}
