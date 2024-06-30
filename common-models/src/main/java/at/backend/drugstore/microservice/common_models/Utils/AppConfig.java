package at.backend.drugstore.microservice.common_models.Utils;

import at.backend.drugstore.microservice.common_models.ExternalService.Employee.ExternalEmployeeService;
import at.backend.drugstore.microservice.common_models.ExternalService.Employee.ExternalEmployeeServiceImpl;
import at.backend.drugstore.microservice.common_models.ExternalService.Inventory.ExternalInventoryService;
import at.backend.drugstore.microservice.common_models.ExternalService.Inventory.ExternalInventoryServiceImpl;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    public ExternalProductService externalProductService(RestTemplate restTemplate) {
        return new ExternalProductServiceImpl(restTemplate);
    }

    @Bean
    public ExternalInventoryService externalInventoryService(RestTemplate restTemplate) {
        return new ExternalInventoryServiceImpl(restTemplate);
    }

    @Bean
    public ExternalEmployeeService externalEmployeeService(RestTemplate restTemplate) {
        return new ExternalEmployeeServiceImpl(restTemplate);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
