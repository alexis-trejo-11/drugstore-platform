package microservice.ecommerce_payment_service.Config;

import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientServiceImpl;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductServiceImpl;
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
    public ExternalClientService externalClientService(RestTemplate restTemplate) {
        return new ExternalClientServiceImpl(restTemplate);
    }
}