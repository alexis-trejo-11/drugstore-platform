package at.backend.drugstore.microservice.common_models.GlobalFacadeService.Inventory;

import at.backend.drugstore.microservice.common_models.DTOs.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class InventoryFacadeServiceImpl implements InventoryFacadeService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryFacadeServiceImpl.class);
    private final RestTemplate restTemplate;

    private final String inventoryServiceUrl = "http://10.212.82.114:8083";

    public InventoryFacadeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Async("taskExecutor")
    public Result<String> updateStockBySaleItemDTO(List<SaleItemDTO> saleItemDTOS) {
            String updateUrl = inventoryServiceUrl + "/stock/update";
            logger.info("Updating stock with URL: {}", updateUrl);
            logger.info("Request Payload: {}", saleItemDTOS);

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
                HttpEntity<List<SaleItemDTO>> requestEntity = new HttpEntity<>(saleItemDTOS, headers);

                ResponseEntity<Void> responseEntity = restTemplate.exchange(
                        updateUrl,
                        HttpMethod.PUT,
                        requestEntity,
                        Void.class
                );

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    return Result.success("Stock updated successfully");
                } else {
                    String errorMessage = "Error response from Inventory Service: " + responseEntity.getStatusCode();
                    logger.error(errorMessage);
                    return Result.error(errorMessage);
                }
            } catch (Exception e) {
                logger.error("An error occurred while updating stock: {}", e.getMessage(), e);
                return Result.error("An error occurred while updating stock: " + e.getMessage());
            }
    }
}
