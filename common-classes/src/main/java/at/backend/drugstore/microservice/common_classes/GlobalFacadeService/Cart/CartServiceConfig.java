package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.Supplier;

@Configuration
public class CartServiceConfig {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    public Supplier<String> cartServiceUrlProvider() {
        return () -> {
            List<ServiceInstance> instances = discoveryClient.getInstances("ECOMMERCE_CART-SERVICE");
            if (instances.isEmpty()) {
                throw new IllegalStateException("Cart is not available");
            }
            return instances.get(0).getUri().toString();
        };
    }

    @Bean
    public CartFacadeService cartFacadeService(RestTemplate restTemplate) {
        return new CartFacadeFacadeServiceImpl(restTemplate, cartServiceUrlProvider());
    }
}
