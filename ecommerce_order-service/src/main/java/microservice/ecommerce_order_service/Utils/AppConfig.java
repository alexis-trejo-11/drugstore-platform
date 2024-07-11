package microservice.ecommerce_order_service.Utils;

import at.backend.drugstore.microservice.common_models.ExternalService.Adress.ExternalAddressService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Primary
    public ExternalClientService externalClientService(RestTemplate restTemplate) {
        return new ExternalClientServiceImpl(restTemplate);
    }

    @Bean
    @Primary
    public ExternalAddressService externalAddressService(RestTemplate restTemplate) {
        return new ExternalAddressService(restTemplate);
    }
}