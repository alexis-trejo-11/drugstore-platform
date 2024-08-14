package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.ESale;

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
public class ESaleFacadeServiceConfig {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    @Qualifier("eSaleServiceUrlProvider")
    public Supplier<String> eSaleServiceUrlProvider() {
        return () -> {
            List<ServiceInstance> instances = discoveryClient.getInstances("ESALE-SERVICE");
            if (instances.isEmpty()) {
                throw new IllegalStateException("ESale service is not available");
            }
            return instances.get(0).getUri().toString();
        };
    }

    @Bean
    @Qualifier("eSaleFacadeService")
    public ESaleFacadeService eSaleFacadeService(
            RestTemplate restTemplate,
            @Qualifier("eSaleServiceUrlProvider") Supplier<String> eSaleServiceUrlProvider) {
        return new ESaleFacadeServiceImpl(restTemplate, eSaleServiceUrlProvider);
    }
}
