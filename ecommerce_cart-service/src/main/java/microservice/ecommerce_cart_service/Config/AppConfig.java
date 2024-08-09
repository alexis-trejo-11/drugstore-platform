package microservice.ecommerce_cart_service.Config;

import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Adress.AddressFacadeServiceImpl;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Client.ClientFacadeServiceImpl;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.client.RestTemplate;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Order.OrderFacadeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Order.OrderFacadeServiceImpl;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Payment.EPaymentFacadeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Payment.EPaymentServiceFacadeServiceImpl;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Products.ProductFacadeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Products.ProductFacadeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    public ProductFacadeService externalProductService(RestTemplate restTemplate) {
        return new ProductFacadeServiceImpl(restTemplate);
    }

    @Bean
    public OrderFacadeService externalOrderService(RestTemplate restTemplate) {
        return new OrderFacadeServiceImpl(restTemplate);
    }

    @Bean
    public ClientFacadeService externalClientService(RestTemplate restTemplate) {
        return new ClientFacadeServiceImpl(restTemplate);
    }

    @Bean
    public AddressFacadeServiceImpl externalAddressService(RestTemplate restTemplate) {
        return new AddressFacadeServiceImpl(restTemplate);
    }

    @Bean
    public EPaymentFacadeService externalPaymentService(RestTemplate restTemplate) {
        return new EPaymentServiceFacadeServiceImpl(restTemplate);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}