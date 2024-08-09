package microservice.sale_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Inventory.InventoryFacadeService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.sale_service.Model.PhysicalSale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryService {

    private final InventoryFacadeService inventoryFacadeService;

    @Autowired
    public InventoryService(InventoryFacadeService inventoryFacadeService) {
        this.inventoryFacadeService = inventoryFacadeService;
    }

    public Result<String> updateInventory(PhysicalSale sale) {
        List<SaleItemDTO> saleItemDTOS = new ArrayList<>();
        for (var saleItem : sale.getSaleItems()) {
            SaleItemDTO saleItemDTO = new SaleItemDTO();
            saleItemDTO.setProductId(saleItem.getProductId());
            saleItemDTO.setProductQuantity(saleItem.getProductQuantity());
            saleItemDTOS.add(saleItemDTO);
        }

        return inventoryFacadeService.updateStockBySaleItemDTO(saleItemDTOS);
    }
}
