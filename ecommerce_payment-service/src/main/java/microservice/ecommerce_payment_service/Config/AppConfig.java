package microservice.ecommerce_payment_service.Config;

import at.backend.drugstore.microservice.common_models.ExternalService.Adress.ExternalAddressService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientServiceImpl;
import at.backend.drugstore.microservice.common_models.ExternalService.DigitalSale.ExternalDigitalSaleImpl;
import at.backend.drugstore.microservice.common_models.ExternalService.Order.ExternalOrderService;
import at.backend.drugstore.microservice.common_models.ExternalService.Order.ExternalOrderServiceImpl;
import at.backend.drugstore.microservice.common_models.ExternalService.Payment.ExternalPaymentService;
import at.backend.drugstore.microservice.common_models.ExternalService.Payment.ExternalPaymentServiceServiceImpl;
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
    @Primary
    public ExternalClientService externalClientService(RestTemplate restTemplate) {
        return new ExternalClientServiceImpl(restTemplate);
    }

    @Bean
    public ExternalAddressService externalAddressService(RestTemplate restTemplate) {
        return new ExternalAddressService(restTemplate);
    }

    @Bean
    public ExternalOrderService externalOrderService(RestTemplate restTemplate) {
        return new ExternalOrderServiceImpl(restTemplate);
    }

    @Bean
    public ExternalDigitalSaleImpl externalDigitalSale(RestTemplate restTemplate) {
        return new ExternalDigitalSaleImpl(restTemplate);
    }
}