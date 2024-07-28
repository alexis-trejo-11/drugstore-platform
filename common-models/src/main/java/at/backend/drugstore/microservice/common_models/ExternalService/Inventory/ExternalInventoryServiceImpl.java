package at.backend.drugstore.microservice.common_models.ExternalService.Inventory;

import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ExternalInventoryServiceImpl implements ExternalInventoryService {

    private final RestTemplate restTemplate;

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Autowired
    public ExternalInventoryServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public Result<String> updateStockBySaleItemDTO(List<SaleItemDTO> saleItemDTOS) {
        // Prepare the URL and headers
        String updateUrl = inventoryServiceUrl + "/stock/update";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the request body
        HttpEntity<List<SaleItemDTO>> requestEntity = new HttpEntity<>(saleItemDTOS, headers);

        try {
            // Make the PUT request to the inventory service
            ResponseEntity<Void> responseEntity = restTemplate.exchange(
                    updateUrl,
                    HttpMethod.PUT,
                    requestEntity,
                    Void.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return Result.success("Stock updated successfully");
            } else {
                return Result.error("Failed to update stock in inventory service");
            }
        } catch (Exception e) {
            return Result.error("An error occurred while updating stock: " + e.getMessage());
        }
    }

}
