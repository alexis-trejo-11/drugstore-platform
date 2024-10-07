package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Cart;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Service
public class CartFacadeFacadeServiceImpl implements CartFacadeService {

    private final RestTemplate restTemplate;
    private final Supplier<String> cartServiceUrlProvider;

    public CartFacadeFacadeServiceImpl(RestTemplate restTemplate, Supplier<String> cartServiceUrlProvider) {
        this.restTemplate = restTemplate;
        this.cartServiceUrlProvider = cartServiceUrlProvider;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> createClientCart(Long clientId) {
        return CompletableFuture.runAsync(() -> {
            log.info("Starting to create client cart for client ID: {}", clientId);
            String url = cartServiceUrlProvider.get() + "/v1/drugstore/ecommerce-carts/create/" + clientId;
                ResponseEntity<ResponseWrapper<Void>> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<ResponseWrapper<Void>>() {},
                        clientId
                );
                if (responseEntity.getStatusCode().is4xxClientError() && responseEntity.getBody() != null) {
                    log.error("Failed to create client cart with status code: {}", responseEntity.getStatusCode());
                    throw new RuntimeException(responseEntity.getBody().getMessage());
                }
                log.info("Successfully created client cart for client ID: {}", clientId);
        });
    }
}