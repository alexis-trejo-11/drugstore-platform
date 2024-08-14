package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.Supplier;

@Configuration
public class EmployeeFacadeServiceConfig {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Bean
    @Qualifier("employeeServiceUrlProvider")
    public Supplier<String> employeeServiceUrlProvider() {
        return () -> {
            List<ServiceInstance> instances = discoveryClient.getInstances("EMPLOYEE-SERVICE");
            if (instances.isEmpty()) {
                throw new IllegalStateException("Employee service is not available");
            }
            return instances.get(0).getUri().toString();
        };
    }

    @Bean
    @Qualifier("employeeFacadeService")
    public EmployeeFacadeService employeeFacadeService(
            RestTemplate restTemplate,
            @Qualifier("employeeServiceUrlProvider") Supplier<String> urlProvider) {
        return new EmployeeFacadeServiceImpl(restTemplate, urlProvider);
    }
}

