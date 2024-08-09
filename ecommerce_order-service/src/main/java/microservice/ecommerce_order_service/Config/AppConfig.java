package microservice.ecommerce_order_service.Config;

import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Adress.AddressFacadeServiceImpl;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Client.ClientFacadeServiceImpl;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Payment.EPaymentFacadeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Payment.EPaymentServiceFacadeServiceImpl;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {


    @Bean
    public ClientFacadeService externalClientService(RestTemplate restTemplate) {
        return new ClientFacadeServiceImpl(restTemplate);
    }

    @Bean
        public EPaymentFacadeService externalPaymentService(RestTemplate restTemplate) {
        return new EPaymentServiceFacadeServiceImpl(restTemplate);
    }

    @Bean
    @Primary
    public AddressFacadeServiceImpl externalAddressService(RestTemplate restTemplate) {
        return new AddressFacadeServiceImpl(restTemplate);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}