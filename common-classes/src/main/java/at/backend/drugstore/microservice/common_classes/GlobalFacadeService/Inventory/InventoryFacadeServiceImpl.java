package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Inventory;

import at.backend.drugstore.microservice.common_classes.DTOs.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Service
public class InventoryFacadeServiceImpl implements InventoryFacadeService {

    private final RestTemplate restTemplate;
    private final Supplier<String> inventoryServiceUrlProvider;

    public InventoryFacadeServiceImpl(RestTemplate restTemplate, Supplier<String> inventoryServiceUrlProvider) {
        this.restTemplate = restTemplate;
        this.inventoryServiceUrlProvider = inventoryServiceUrlProvider;
    }

    @Override
    @Async("taskExecutor")
    public Result<String> updateStockBySaleItemDTO(List<SaleItemDTO> saleItemDTOS) {
        String updateUrl = inventoryServiceUrlProvider.get() + "/stock/update";
        log.info("Updating stock with URL: {}", updateUrl);
        log.info("Request Payload: {}", saleItemDTOS);

        // Set up the HTTP headers, specifying that the content type is JSON
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity<List<SaleItemDTO>> requestEntity = new HttpEntity<>(saleItemDTOS, headers);

        ResponseEntity<ResponseWrapper<Void>> responseEntity = restTemplate.exchange(
                updateUrl,
                HttpMethod.PUT,
                requestEntity,
                new ParameterizedTypeReference<ResponseWrapper<Void>>() {
                }
        );

        ResponseWrapper<Void> responseEntityBody = responseEntity.getBody();
        assert responseEntityBody != null;

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return Result.success(responseEntityBody.getMessage());
        } else {
            return Result.error(responseEntityBody.getMessage());
        }
    }

}