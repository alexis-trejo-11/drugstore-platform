package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client;

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
public class ClientServiceConfig {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    @Qualifier("clientServiceUrlProvider")
    public Supplier<String> clientServiceUrlProvider() {
        return () -> {
            List<ServiceInstance> instances = discoveryClient.getInstances("CLIENT-SERVICE");
            if (instances.isEmpty()) {
                throw new IllegalStateException("Client service is not available");
            }
            return instances.get(0).getUri().toString();
        };
    }

    @Bean
    @Qualifier("clientFacadeService")
    public ClientFacadeService clientFacadeService(
            RestTemplate restTemplate,
            @Qualifier("clientServiceUrlProvider") Supplier<String> clientServiceUrlProvider) {
        return new ClientFacadeServiceImpl(restTemplate, clientServiceUrlProvider);
    }
}
