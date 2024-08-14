package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Products;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

@Slf4j
@Service
public class ProductFacadeServiceImpl implements ProductFacadeService {

    private final RestTemplate restTemplate;
    private final Supplier<String> productServiceUrlProvider;

    public ProductFacadeServiceImpl(RestTemplate restTemplate, Supplier<String> productServiceUrlProvider) {
        this.restTemplate = restTemplate;
        this.productServiceUrlProvider = productServiceUrlProvider;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<ProductDTO>> getProductsByIds(List<Long> productIds) {
        String url = productServiceUrlProvider.get() + "/v1/api/products/by-ids";

        Map<String, List<Long>> request = new HashMap<>();
        request.put("productIds", productIds);

        return CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<ResponseWrapper<List<ProductDTO>>> response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        new HttpEntity<>(request, createJsonHeaders()),
                        new ParameterizedTypeReference<ResponseWrapper<List<ProductDTO>>>() {}
                );

                if (response.getStatusCode() == HttpStatus.OK) {
                    log.info("Received response from Product Service");
                    return Objects.requireNonNull(response.getBody()).getData();
                } else {
                    log.error("Error response from Product Service: {}", response.getStatusCode());
                    throw new CompletionException(new RuntimeException("Error response from Product Service"));
                }
            } catch (Exception e) {
                log.error("An error occurred while fetching products: {}", e.getMessage());
                throw new CompletionException(e);
            }
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<ProductDTO>> getProductById(Long productId) {
        String url = productServiceUrlProvider.get() + "/v1/api/products/" + productId;

        return CompletableFuture.supplyAsync(() -> {
                ResponseWrapper<ProductDTO> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ResponseWrapper<ProductDTO>>() {}
                ).getBody();
            assert response != null;

            if (response.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                    log.warn("Product with ID {} not found", productId);
                    return Result.error(response.getMessage());
            }

            log.info("Product information retrieved successfully for product ID: {}", productId);
            return Result.success(response.getData());
        });
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
