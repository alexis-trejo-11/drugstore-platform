package at.backend.drugstore.microservice.common_models.ExternalService.Products;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class ExternalProductServiceImpl implements ExternalProductService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ExternalProductServiceImpl.class);

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Autowired
    public ExternalProductServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<ProductDTO>> getProductsByIds(List<Long> productIds) {
        String productUrl = productServiceUrl + "/v1/api/products/by-ids";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, List<Long>> request = new HashMap<>();
        request.put("productIds", productIds);

        HttpEntity<Map<String, List<Long>>> requestEntity = new HttpEntity<>(request, headers);

        return CompletableFuture.supplyAsync(() -> {
                ResponseEntity<ApiResponse<List<ProductDTO>>> productResponseEntity = restTemplate.exchange(
                        productUrl,
                        HttpMethod.POST,
                        requestEntity,
                        new ParameterizedTypeReference<ApiResponse<List<ProductDTO>>>() {}
                );

                logger.info("Received response from Product Service with status: {}", productResponseEntity.getStatusCode());

                if (productResponseEntity.getStatusCode() != HttpStatus.OK && productResponseEntity.getBody() != null) {
                    logger.error("Error in response from Product Service: {}", productResponseEntity.getBody().getMessage());
                    throw new RuntimeException();
                } else {
                    return Objects.requireNonNull(productResponseEntity.getBody()).getData();
                }
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<ProductDTO>> getProductById(Long productId) {
        String productUrl = productServiceUrl + "/v1/api/products/" + productId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Fetching product information for product ID: {}", productId);

                ResponseEntity<ApiResponse<ProductDTO>> productResponseEntity = restTemplate.exchange(
                        productUrl,
                        HttpMethod.GET,
                        requestEntity,
                        new ParameterizedTypeReference<ApiResponse<ProductDTO>>() {}
                );

                HttpStatus statusCode = productResponseEntity.getStatusCode();
                ApiResponse<ProductDTO> productBody = productResponseEntity.getBody();

                if (statusCode == HttpStatus.NOT_FOUND && productBody != null) {
                    logger.warn("Product with ID {} not found", productId);
                    return new Result<>(false, null, "Product Not Found");
                } else {
                    logger.info("Product information retrieved successfully for product ID: {}", productId);
                    assert productBody != null;
                    return Result.success(productBody.getData());
                }
            } catch (Exception e) {
                logger.error("An unexpected error occurred while retrieving product with ID {}: {}", productId, e.getMessage());
                throw new CompletionException(e);
            }
        });
    }
}