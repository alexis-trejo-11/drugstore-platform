package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Cart;

import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
public class CartFacadeFacadeServiceImpl implements CartFacadeService {

    private final RestTemplate restTemplate;
    private final Supplier<String> cartServiceUrlProvider;
    private final Logger logger = LoggerFactory.getLogger(CartFacadeFacadeServiceImpl.class);

    public CartFacadeFacadeServiceImpl(RestTemplate restTemplate, Supplier<String> cartServiceUrlProvider) {
        this.restTemplate = restTemplate;
        this.cartServiceUrlProvider = cartServiceUrlProvider;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> createClientCart(Long clientId) {
        return CompletableFuture.runAsync(() -> {
            try {
                String url = cartServiceUrlProvider.get() + "/v1/api/ecommerce/carts/create/{clientId}";
                ResponseEntity<ResponseWrapper> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        HttpEntity.EMPTY,
                        ResponseWrapper.class,
                        clientId
                );

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    logger.info("Successfully created client cart for client ID: {}", clientId);
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