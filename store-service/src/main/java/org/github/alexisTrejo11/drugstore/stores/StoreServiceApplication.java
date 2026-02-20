package org.github.alexisTrejo11.drugstore.stores;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = { "org.github.alexisTrejo11.drugstore.stores", "libs_kernel" })
@EnableCaching
@EnableAspectJAutoProxy
public class StoreServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(StoreServiceApplication.class, args);
  }

}
