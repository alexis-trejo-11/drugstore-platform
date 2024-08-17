package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.Supplier;

@Configuration
public class InventoryFacadeServiceConfig {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    @Qualifier("inventoryServiceUrlProvider")
    public Supplier<String> inventoryServiceUrlProvider() {
        return () -> {
            List<ServiceInstance> instances = discoveryClient.getInstances("INVENTORY-SERVICE");
            if (instances.isEmpty()) {
                throw new IllegalStateException("Inventory service is not available");
            }
            return instances.get(0).getUri().toString();
        };
    }

    @Bean
    public InventoryFacadeService inventoryFacadeService(RestTemplate restTemplate) {
        return new InventoryFacadeServiceImpl(restTemplate, inventoryServiceUrlProvider()) {
        };
    }
}
