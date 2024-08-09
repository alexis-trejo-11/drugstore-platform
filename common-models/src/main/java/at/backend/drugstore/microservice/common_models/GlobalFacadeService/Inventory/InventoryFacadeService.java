package at.backend.drugstore.microservice.common_models.GlobalFacadeService.Inventory;

import at.backend.drugstore.microservice.common_models.DTOs.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.List;

public interface InventoryFacadeService {
    Result<String> updateStockBySaleItemDTO(List<SaleItemDTO> saleItemDTOS);
}
