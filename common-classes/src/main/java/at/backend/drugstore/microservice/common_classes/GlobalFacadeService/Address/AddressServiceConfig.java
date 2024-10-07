package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Address;

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
public class AddressServiceConfig {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    @Qualifier("addressServiceUrlProvider")
    public Supplier<String> addressServiceUrlProvider() {
        return () -> {
            List<ServiceInstance> instances = discoveryClient.getInstances("ADDRESS-SERVICE");
            if (instances.isEmpty()) {
                throw new IllegalStateException("Address service is not available");
            }
            return instances.get(0).getUri().toString();
        };
    }

    @Bean
    @Qualifier("addressFacadeService")
    public AddressFacadeService addressFacadeService(
            RestTemplate restTemplate,
            @Qualifier("addressServiceUrlProvider") Supplier<String> addressServiceUrlProvider) {
        return new AddressFacadeServiceImpl(restTemplate, addressServiceUrlProvider);
    }
}
