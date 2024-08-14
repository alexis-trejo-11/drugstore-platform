package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Products;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class ProductFacadeServiceImpl implements ProductFacadeService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ProductFacadeServiceImpl.class);

    private final String productServiceUrl = "http://product-service:8083";

    public ProductFacadeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<ProductDTO>> getProductsByIds(List<Long> productIds) {
        String url = productServiceUrl + "/v1/api/products/by-ids";

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
                    logger.info("Received response from Product Service");
                    return Objects.requireNonNull(response.getBody()).getData();
                } else {
                    logger.error("Error response from Product Service: {}", response.getStatusCode());
                    throw new CompletionException(new RuntimeException("Error response from Product Service"));
                }
            } catch (Exception e) {
                logger.error("An error occurred while fetching products: {}", e.getMessage());
                throw new CompletionException(e);
            }
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<ProductDTO>> getProductById(Long productId) {
        String url = productServiceUrl + "/v1/api/products/" + productId;

        return CompletableFuture.supplyAsync(() -> {
                ResponseWrapper<ProductDTO> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ResponseWrapper<ProductDTO>>() {}
                ).getBody();
            assert response != null;

            if (response.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                    logger.warn("Product with ID {} not found", productId);
                    return Result.error(response.getMessage());
            }

            logger.info("Product information retrieved successfully for product ID: {}", productId);
            return Result.success(response.getData());
        });
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
