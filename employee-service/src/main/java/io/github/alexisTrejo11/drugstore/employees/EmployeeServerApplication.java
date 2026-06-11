package io.github.alexisTrejo11.drugstore.employees;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "io.github.alexisTrejo11.drugstore.employees",
    "libs_kernel"
})
public class EmployeeServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(EmployeeServerApplication.class, args);
  }
}
