package io.github.alexisTrejo11.drugstore.address;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "io.github.alexisTrejo11.drugstore.address",
    "libs_kernel"
})
public class AddressServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(AddressServerApplication.class, args);
  }
}
