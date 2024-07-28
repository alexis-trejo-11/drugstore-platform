package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryStockDTO;
import at.backend.drugstore.microservice.common_models.DTO.Inventory.ProductStockDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.inventory_service.Model.Inventory;
import microservice.inventory_service.Repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    private final InventoryRepository inventoryRepository;
    private final ExternalProductService externalProductService;

    @Autowired
    public StockServiceImpl(InventoryRepository inventoryRepository, ExternalProductService externalProductService) {
        this.inventoryRepository = inventoryRepository;
        this.externalProductService = externalProductService;
    }

    @Override
    @Async
    @Transactional
    public Result<ProductDTO> validateExistingProduct(Long productId) {
        Result<ProductDTO> productResult = externalProductService.getProductById(productId);
        if (!productResult.isSuccess()) {
            new Result<>(false, null, productResult.getErrorMessage());
        }

        return Result.success(productResult.getData());
    }

    @Override
    @Async
    @Transactional
    public Result<Void> updateStockFromSale(List<SaleItemDTO> saleItemDTOS) {
        for (var saleItemDTO : saleItemDTOS) {
            List<Inventory> inventories = inventoryRepository.findByProductId(saleItemDTO.getProductId());

            // Sort inventories by expiration date
            inventories.sort(Comparator.comparing(Inventory::getExpirationDate));

            int productQuantity = saleItemDTO.getProductQuantity();

            boolean processed = false;

            for (Inventory inventory : inventories) {
                int currentInventory = inventory.getQuantity();

                if (currentInventory >= productQuantity) {
                    // If the current inventory can fulfill the required quantity
                    inventory.setQuantity(currentInventory - productQuantity);
                    inventoryRepository.saveAndFlush(inventory);

                    processed = true;
                    break;
                } else {
                    // If the current inventory cannot fulfill the required quantity
                    productQuantity -= currentInventory;
                    inventory.setQuantity(0);
                    inventoryRepository.saveAndFlush(inventory);
                }
            }

            if (!processed) {
                return Result.error("Not enough inventory for product ID: " + saleItemDTO.getProductId());
            }
        }
        return Result.success();
    }


    @Override
    @Async
    @Transactional
    public ProductStockDTO getCurrentStockByProduct(Long productId, ProductDTO productDTO) {
        List<Inventory> inventories = inventoryRepository.findByProductId(productId);
        if (inventories.isEmpty()) {
            return null;
        }

        return makeProductStockDTO(inventories, productDTO);
    }

    private ProductStockDTO makeProductStockDTO(List<Inventory> inventories, ProductDTO productDTO) {
        ProductStockDTO productStockDTO = new ProductStockDTO();
        productStockDTO.setProductId(productDTO.getId());
        productStockDTO.setProductName(productDTO.getName());
        int totalStock = 0;

        List<InventoryStockDTO> inventoryStockDTOS = new ArrayList<>();
        for (var inventory : inventories) {
            InventoryStockDTO inventoryStockDTO = new InventoryStockDTO();
            inventoryStockDTO.setId(inventory.getId());
            inventoryStockDTO.setBatchNumber(inventory.getBatchNumber());
            inventoryStockDTO.setExpirationDate(inventory.getExpirationDate());
            inventoryStockDTO.setQuantity(inventory.getQuantity());
            totalStock += inventory.getQuantity();

            inventoryStockDTOS.add(inventoryStockDTO);
        }

        productStockDTO.setInventoryStockDTOS(inventoryStockDTOS);
        productStockDTO.setTotalStock(totalStock);

        return productStockDTO;
    }

}





