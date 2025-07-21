package microservice.ecommerce_cart_service.app.infrastructure.external_services;

import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.Product;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.ProductId;
import microservice.ecommerce_cart_service.app.domain.port.out.repositories.ProductFacadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductFacadeServiceImpl implements ProductFacadeService {
    private final RestTemplate restTemplate;
    private final Supplier<String> productServiceUrlProvider;

    @Autowired
    public ProductFacadeServiceImpl(RestTemplate restTemplate, Supplier<String> productServiceUrlProvider) {
        this.restTemplate = restTemplate;
        this.productServiceUrlProvider = productServiceUrlProvider;
    }

    @Override
    public List<Product> findAvailableByIdIn(Set<ProductId> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> idStrings = productIds.stream()
                .map(ProductId::toString)
                .collect(Collectors.toSet());

        String baseUrl = productServiceUrlProvider.get();

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/api/v2/products/batch")
                .queryParam("ids", idStrings)
                .queryParam("active_only", true)
                .encode()
                .toUriString();

        try {
            ResponseEntity<List<Product>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<List<Product>>() {}
            );

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                return responseEntity.getBody();
            } else {
                log.warn("findAvailableByIdIn Product service returned unexpected response for URL {}: Status={}, Body={}",
                        url, responseEntity.getStatusCode(), responseEntity.getBody());
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            log.error("Client error calling Product Service at {}: Status={}, Body={}", url, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new IllegalArgumentException("Failed to retrieve products from external service due to client error.", e);
        } catch (ResourceAccessException e) {
            log.error("Network/Service unavailable error calling Product Service at {}: {}", url, e.getMessage(), e);
            throw new IllegalArgumentException("Product service is currently unavailable.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while calling Product Service at {}: {}", url, e.getMessage(), e);
            throw new IllegalArgumentException("An unexpected error occurred during product retrieval.", e);
        }
    }

    @Override
    public List<Product> findByIdIn(List<ProductId> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> idStrings = productIds.stream()
                .map(ProductId::toString)
                .collect(Collectors.toSet());

        String baseUrl = productServiceUrlProvider.get();

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/api/v2/products/batch")
                .queryParam("ids", idStrings)
                .encode()
                .toUriString();

        try {
            ResponseEntity<List<Product>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<List<Product>>() {}
            );

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                return responseEntity.getBody();
            } else {
                log.warn("findByIdIn Product service returned unexpected response for URL {}: Status={}, Body={}",
                        url, responseEntity.getStatusCode(), responseEntity.getBody());
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            log.error("Client error calling Product Service at {}: Status={}, Body={}", url, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new IllegalArgumentException("Failed to retrieve products from external service due to client error.", e);
        } catch (ResourceAccessException e) {
            log.error("Network/Service unavailable error calling Product Service at {}: {}", url, e.getMessage(), e);
            throw new IllegalArgumentException("Product service is currently unavailable.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while calling Product Service at {}: {}", url, e.getMessage(), e);
            throw new IllegalArgumentException("An unexpected error occurred during product retrieval.", e);
        }
    }
}