package at.backend.drugstore.microservice.common_models.ExternalService.Products;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
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
import java.util.logging.Level;

@Service
public class ExternalProductServiceImpl implements ExternalProductService {

    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Autowired
    public ExternalProductServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Result<List<ProductDTO>> findProducts(List<Long> productIds) {
        String productUrl = productServiceUrl + "/many";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, List<Long>> request = new HashMap<>();
        request.put("productIds", productIds);

        HttpEntity<Map<String, List<Long>>> requestEntity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<ResponseWrapper<List<ProductDTO>>> productResponseEntity = restTemplate.exchange(
                    productUrl,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {});

            if (productResponseEntity.getStatusCode() != HttpStatus.OK) {
                Result result = new Result<>();
                result.setStatus(productResponseEntity.getStatusCode());
                result.setErrorMessage(productResponseEntity.getBody().getMessage());
                return result;
            }

            ResponseWrapper<List<ProductDTO>> products = productResponseEntity.getBody();
            assert products != null;
            return Result.success(products.getData());
        } catch (Exception e) {
            return Result.error("Exception occurred while fetching product data: " + e.getMessage());
        }
    }


    @Override
    public Result<ProductDTO> getProductById(Long productId) {
        String productUrl = productServiceUrl + "/" + productId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseWrapper<ProductDTO>> productResponseEntity = restTemplate.exchange(
                    productUrl,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<ResponseWrapper<ProductDTO>>() {}
            );

            if (productResponseEntity.getStatusCode() != HttpStatus.OK || productResponseEntity.getBody() == null) {
                return new Result<>(false, null, productResponseEntity.getBody().getMessage(), productResponseEntity.getStatusCode());
            } else {
                ResponseWrapper<ProductDTO> response = new ResponseWrapper<>(productResponseEntity.getBody().getData(), null);
                return Result.success(response.getData());
            }
        } catch (Exception e) {
            return new Result<>(false, null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

}
