package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client;

import org.springframework.beans.factory.annotation.Autowired;
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
    public Supplier<String> clientServiceUrlProvider() {
        return () -> {
            List<ServiceInstance> instances = discoveryClient.getInstances("CLIENT-SERVICE");
            if (instances.isEmpty()) {
                throw new IllegalStateException("Client is not available");
            }
            return instances.get(0).getUri().toString();
        };
    }

    @Bean
    public ClientFacadeService clientFacadeService(RestTemplate restTemplate) {
        return new ClientFacadeServiceImpl(restTemplate, clientServiceUrlProvider());
    }
}
