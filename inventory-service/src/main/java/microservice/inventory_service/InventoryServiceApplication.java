package microservice.inventory_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"microservice.inventory_service", "libs_kernel.log.audit"})
public class InventoryServiceApplication {
    public  static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
