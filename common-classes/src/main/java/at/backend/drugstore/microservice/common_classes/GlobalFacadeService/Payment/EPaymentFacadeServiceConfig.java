package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Payment;

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
public class EPaymentFacadeServiceConfig {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    @Qualifier("ePaymentServiceUrlProvider")
    public Supplier<String> ePaymentServiceUrlProvider() {
        return () -> {
            List<ServiceInstance> instances = discoveryClient.getInstances("EPAYMENT-SERVICE");
            if (instances.isEmpty()) {
                throw new IllegalStateException("Payment service is not available");
            }
            return instances.get(0).getUri().toString();
        };
    }

    @Bean
    @Qualifier("ePaymentFacadeService")
    public EPaymentFacadeService ePaymentFacadeService(
            RestTemplate restTemplate,
            @Qualifier("ePaymentServiceUrlProvider") Supplier<String> ePaymentServiceUrlProvider) {
        return new EPaymentFacadeServiceImpl(restTemplate, ePaymentServiceUrlProvider);
    }
}
