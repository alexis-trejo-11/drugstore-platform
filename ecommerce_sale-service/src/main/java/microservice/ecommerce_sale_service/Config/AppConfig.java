package microservice.ecommerce_sale_service.Config;

import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Products.ProductFacadeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Products.ProductFacadeServiceImpl;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public ProductFacadeService externalProductService(RestTemplate restTemplate) {
        return new ProductFacadeServiceImpl(restTemplate);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}