package at.backend.drugstore.microservice.common_models.GlobalFacadeService.Cart;

import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class CartFacadeFacadeServiceImpl implements CartFacadeService {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(CartFacadeFacadeServiceImpl.class);

    private final String cartServiceUrl = "http://ecommercer_cart-service:8086";

    public CartFacadeFacadeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> createClientCart(Long clientId) {
        return CompletableFuture.runAsync(() -> {
            try {
                String url = cartServiceUrl + "/v1/api/ecommerce/carts/create/{clientId}";
                ResponseEntity<ResponseWrapper> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        HttpEntity.EMPTY,
                        ResponseWrapper.class,
                        clientId
                );

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    // Successfully created client cart
                } else {
                    logger.error("Failed to create client cart with status code: {}", responseEntity.getStatusCode());
                    throw new RuntimeException("Failed to create client cart");
                }
            } catch (Exception e) {
                logger.error("Failed to create client cart: {}", e.getMessage());
                throw new RuntimeException("Failed to create client cart", e);
            }
        });
    }
}
