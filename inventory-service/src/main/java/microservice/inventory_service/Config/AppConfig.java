package microservice.inventory_service.Config;

import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Employee.ExternalEmployeeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Employee.ExternalEmployeeServiceImpl;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Products.ProductFacadeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Products.ProductFacadeServiceImpl;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    public ProductFacadeService externalProductService(RestTemplate restTemplate) {
        return new ProductFacadeServiceImpl(restTemplate);
    }

    @Bean
    public ExternalEmployeeService externalEmployeeService(RestTemplate restTemplate) {
        return new ExternalEmployeeServiceImpl(restTemplate);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
