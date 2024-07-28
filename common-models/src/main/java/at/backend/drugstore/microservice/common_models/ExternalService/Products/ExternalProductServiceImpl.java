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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public Result<List<ProductDTO>> getProductsByIds(List<Long> productIds) {
        String productUrl = productServiceUrl + "/by-ids";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, List<Long>> request = new HashMap<>();
        request.put("productIds", productIds);

        HttpEntity<Map<String, List<Long>>> requestEntity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<ApiResponse<List<ProductDTO>>> productResponseEntity = restTemplate.exchange(
                    productUrl,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ApiResponse<List<ProductDTO>>>() {
                    });

            logger.info("Received response from Product Service with status: {}", productResponseEntity.getStatusCode());

            if (productResponseEntity.getStatusCode() == HttpStatus.OK && productResponseEntity.getBody() != null) {
                logger.debug("Response Body: {}", productResponseEntity.getBody());
                return Result.success(productResponseEntity.getBody().getData());
            } else {
                logger.error("Error in response from Product Service: {}", productResponseEntity.getBody().getMessage());
                return Result.error(productResponseEntity.getBody().getMessage());
            }
        } catch (Exception e) {
            logger.error("Exception occurred while calling Product Service", e);
            throw new RuntimeException(e);
        }
    }
    @Override
    public Result<ProductDTO> getProductById(Long productId) {
        String productUrl = productServiceUrl + "/" + productId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        try {
            logger.info("Fetching product information for product ID: {}", productId);

            ResponseEntity<ApiResponse<ProductDTO>> productResponseEntity = restTemplate.exchange(
                    productUrl,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<ApiResponse<ProductDTO>>() {
                    }
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
            throw  new RuntimeException(e);
        }
    }
}
