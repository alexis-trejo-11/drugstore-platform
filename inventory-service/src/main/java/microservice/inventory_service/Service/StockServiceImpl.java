package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryStockDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.ProductStockDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Products.ProductFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.inventory_service.Model.Inventory;
import microservice.inventory_service.Repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class StockServiceImpl implements StockService {

    private final InventoryRepository inventoryRepository;
    private final ProductFacadeService productFacadeService;

    @Autowired
    public StockServiceImpl(InventoryRepository inventoryRepository, ProductFacadeService productFacadeService) {
        this.inventoryRepository = inventoryRepository;
        this.productFacadeService = productFacadeService;
    }

    @Override
    @Async("taskExecutor")
    @Cacheable(value = "productCache", key = "#productId")
    public CompletableFuture<Result<ProductDTO>> validateExistingProduct(Long productId) {
        return productFacadeService.getProductById(productId)
                .thenApplyAsync(productResult -> {
                    if (!productResult.isSuccess()) {
                        return new Result<>(false, null, productResult.getErrorMessage());
                    }
                    return Result.success(productResult.getData());
                });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> updateStockFromSale(List<SaleItemDTO> saleItemDTOS) {
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
                return CompletableFuture.completedFuture(Result.error("Not enough inventory for product ID: " + saleItemDTO.getProductId()));
            }
        }
        return CompletableFuture.completedFuture(Result.success());
    }

    @Override
    @Async("taskExecutor")
    @Cacheable(value = "productStockCache", key = "#productId")
    public CompletableFuture<ProductStockDTO> getCurrentStockByProduct(Long productId, ProductDTO productDTO) {
        List<Inventory> inventories = inventoryRepository.findByProductId(productId);
        if (inventories.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.completedFuture(makeProductStockDTO(inventories, productDTO));
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
