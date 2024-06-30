package at.backend.drugstore.microservice.common_models.ExternalService.Inventory;

import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.List;

public interface ExternalInventoryService {
    Result<String> updateStockBySaleItemDTO(List<SaleItemDTO> saleItemDTOS);
}
