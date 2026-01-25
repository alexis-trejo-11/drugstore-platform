package microservice.cart_service.config.externalService;

/*
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Supplier;

@Configuration
public class ProductServiceConfig {

  @Autowired
  private DiscoveryClient discoveryClient;

  @Bean
  @Qualifier("productServiceUrlProvider")
  public Supplier<String> productServiceUrlProvider() {
    return () -> {
      List<InstanceInfo> instances = discoveryClient.getInstancesById("PRODUCT-SERVICE");
      if (instances.isEmpty()) {
        throw new IllegalStateException("Product service is not available");
      }
      return instances.get(0).getHomePageUrl();
    };
  }

  /*
  @Bean
  @Qualifier("productService")
  public ProductFacadeServiceImpl productFacadeService(
      RestTemplate restTemplate,
      @Qualifier("productServiceUrlProvider") Supplier<String> productServiceUrlProvider) {
    return new ProductFacadeServiceImpl(restTemplate, productServiceUrlProvider) {
    };
  }

}
   */
