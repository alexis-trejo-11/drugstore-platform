package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Inventory.ProductStockDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.inventory_service.Model.Inventory;
import microservice.inventory_service.Repository.InventoryRepository;
import microservice.inventory_service.Utils.ModelTransform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class StockService {

    private final InventoryRepository inventoryRepository;

    private final ExternalProductService externalProductService;

    @Autowired
    public StockService(InventoryRepository inventoryRepository, ExternalProductService externalProductService) {
        this.inventoryRepository = inventoryRepository;
        this.externalProductService = externalProductService;
    }

    @Async
    @Transactional
    public CompletableFuture<Result<Void>> updateStockFromSale(List<SaleItemDTO> saleItemDTOS) {
        try {
            for (var saleItemDTO : saleItemDTOS) {
                List<Inventory> inventories = inventoryRepository.findByProductId(saleItemDTO.getProductId());

                // Sort inventories by expiration date
                inventories.sort(Comparator.comparing(Inventory::getExpirationDate));

                int productQuantity = saleItemDTO.getQuantity();

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
                    return CompletableFuture.completedFuture(Result.error("Not enough inventory for product ID: " + saleItemDTO.getProductId()));
                }
            }

            return CompletableFuture.completedFuture(Result.success());
        } catch (Exception e) {
            // Log the exception and return an error result
            return CompletableFuture.failedFuture(new Throwable("An Error Occurred While Updating Stock", e));
        }
    }


    @Async
    @Transactional
    public CompletableFuture<Result<ProductStockDTO>> getCurrentStockByProduct(Long productId) {
        try {
            List<Inventory> inventories = inventoryRepository.findByProductId(productId);
            if (inventories.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("No Inventories Founded With Product Id:" + productId + "Not Found"));
            }

            // Get Product Connecting With Product-Service
            Result<ProductDTO> productResult = externalProductService.findProductById(productId);

            ProductStockDTO productStockDTO = ModelTransform.inventoryToProductStockDTO(inventories, productResult.getData());
            return CompletableFuture.completedFuture(Result.success(productStockDTO));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new Throwable("An Error Occurred While Getting Stock", e));
        }

    }

}





