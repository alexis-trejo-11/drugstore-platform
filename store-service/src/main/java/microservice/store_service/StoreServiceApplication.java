package microservice.store_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = { "microservice.store_service", "libs_kernel.log.audit" })
@EnableCaching
@EnableAspectJAutoProxy
public class StoreServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(StoreServiceApplication.class, args);
  }

}
