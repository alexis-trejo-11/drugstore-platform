package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Products;

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
public class ProductFacadeServiceConfig {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    @Qualifier("productServiceUrlProvider")
    public Supplier<String> productServiceUrlProvider() {
        return () -> {
            List<ServiceInstance> instances = discoveryClient.getInstances("PRODUCT-SERVICE");
            if (instances.isEmpty()) {
                throw new IllegalStateException("Product service is not available");
            }
            return instances.get(0).getUri().toString();
        };
    }

    @Bean
    @Qualifier("productFacadeService")
    public ProductFacadeService productFacadeService(
            RestTemplate restTemplate,
            @Qualifier("productServiceUrlProvider") Supplier<String> productServiceUrlProvider) {
        return new ProductFacadeServiceImpl(restTemplate, productServiceUrlProvider) {
        };
    }
}
