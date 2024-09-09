package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Order;

import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.ESale.ESaleFacadeService;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.ESale.ESaleFacadeServiceImpl;
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
public class OrderFacadeServiceConfig {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    @Qualifier("orderServiceUrlProvider")
    public Supplier<String> orderServiceUrlProvider() {
        return () -> {
            List<ServiceInstance> instances = discoveryClient.getInstances("EORDER-SERVICE");
            if (instances.isEmpty()) {
                throw new IllegalStateException("Order service is not available");
            }
            return instances.get(0).getUri().toString();
        };
    }

    @Bean
    @Qualifier("orderFacadeService")
    public OrderFacadeService orderFacadeService(
            RestTemplate restTemplate,
            @Qualifier("orderServiceUrlProvider") Supplier<String> orderServiceUrlProvider) {
        return new OrderFacadeServiceImpl(restTemplate, orderServiceUrlProvider);
    }
}
