package microservice.ecommerce_payment_service.Config;

import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Adress.AddressFacadeServiceImpl;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Client.ClientFacadeServiceImpl;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.ESale.ESaleFacadeImpl;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Order.OrderFacadeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Order.OrderFacadeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Primary
    @Bean
    public ClientFacadeService externalClientService(RestTemplate restTemplate) {
        return new ClientFacadeServiceImpl(restTemplate);
    }

    @Bean
    public AddressFacadeServiceImpl externalAddressService(RestTemplate restTemplate) {
        return new AddressFacadeServiceImpl(restTemplate);
    }

    @Bean
    public OrderFacadeService externalOrderService(RestTemplate restTemplate) {
        return new OrderFacadeServiceImpl(restTemplate);
    }

    @Bean
    public ESaleFacadeImpl externalDigitalSale(RestTemplate restTemplate) {
        return new ESaleFacadeImpl(restTemplate);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}