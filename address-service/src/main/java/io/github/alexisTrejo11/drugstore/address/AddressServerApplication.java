package io.github.alexisTrejo11.drugstore.address;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
		scanBasePackages = {
				"io.github.alexisTrejo11.drugstore.address",
				"libs_kernel.config.rate_limit",
				"libs_kernel.security.jwt",
				"libs_kernel.log",
		})
public class AddressServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(AddressServerApplication.class, args);
  }
}
