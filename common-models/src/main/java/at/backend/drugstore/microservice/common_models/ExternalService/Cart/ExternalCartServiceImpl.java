package at.backend.drugstore.microservice.common_models.ExternalService.Cart;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class ExternalCartServiceImpl implements ExternalCartService {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(ExternalCartServiceImpl.class);

    @Value("${ecommerce.cart.service.url}")
    private String ecommerceCartServiceUrl;

    @Autowired
    public ExternalCartServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> createClientCart(Long clientId) {
        String url = ecommerceCartServiceUrl + "/v1/api/ecommerce/carts/create/" + clientId;
        return CompletableFuture.runAsync(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<ApiResponse<Void>>() {}
            );

            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new RuntimeException("Failed to create client cart: " + response.getStatusCode());
            }
        });
    }

}
