package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Inventory;

import at.backend.drugstore.microservice.common_classes.DTOs.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.List;

public interface InventoryFacadeService {
    Result<String> updateStockBySaleItemDTO(List<SaleItemDTO> saleItemDTOS);
}
