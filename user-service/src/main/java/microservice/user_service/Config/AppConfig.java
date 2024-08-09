package microservice.user_service.Config;

import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Cart.CartFacadeFacadeServiceImpl;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Cart.CartFacadeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Client.ClientFacadeServiceImpl;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    public CartFacadeService cartFacadeService(RestTemplate restTemplate) {
        return new CartFacadeFacadeServiceImpl(restTemplate);
    }

    @Bean
    public ClientFacadeService clientFacadeService(RestTemplate restTemplate) {
        return new ClientFacadeServiceImpl(restTemplate);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
