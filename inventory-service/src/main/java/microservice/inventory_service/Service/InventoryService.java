package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Employee.ExternalEmployeeService;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductServiceImpl;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryDTO;
import microservice.inventory_service.Model.Inventory;
import microservice.inventory_service.Model.InventoryTransaction;
import microservice.inventory_service.Repository.InventoryRepository;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static microservice.inventory_service.Utils.ModelTransform.inventoryToReturnDTO;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ExternalProductService externalProductService;
    private final ExternalEmployeeService externalEmployeeService;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, ExternalProductService externalProductService, ExternalEmployeeService externalEmployeeService) {
        this.inventoryRepository = inventoryRepository;
        this.externalProductService = externalProductService;
        this.externalEmployeeService = externalEmployeeService;
    }

    @Async
    @Transactional
    public CompletableFuture<Result<Void>> createInventory(InventoryInsertDTO inventoryInsertDTO) {
        try {
            // Init Inventory
            Inventory inventory = new Inventory(inventoryInsertDTO);

            // Get Product And Set Connection With Product-Service
            Result<ProductDTO> productResult = externalProductService.getProductById(inventoryInsertDTO.getProductId());
            if (!productResult.isSuccess()) {
                return CompletableFuture.completedFuture(Result.error(productResult.getErrorMessage()));
            }

            // Get Employee And Set Connection With Product-Service
            Result<EmployeeDTO> employeeResult = externalEmployeeService.findEmployeeById(inventoryInsertDTO.getInventoryTransactionInsertDTO().getEmployeeId());
            if (!productResult.isSuccess()) {
                return CompletableFuture.completedFuture(Result.error(employeeResult.getErrorMessage()));
            }

            ProductDTO productDTO = productResult.getData();
            // Set Product Data in Inventory
            inventory.setProductId(productDTO.getId());

            // Init Inventory Transaction And Append Into Inventory Model
            InventoryTransaction inventoryTransaction = new InventoryTransaction(inventoryInsertDTO.getInventoryTransactionInsertDTO(), employeeResult.getData().getId());
            inventoryTransaction.setInventory(inventory);

            if (inventory.getTransactions() == null) {
                List<InventoryTransaction> inventoryTransactionList = new ArrayList<>();
                inventoryTransactionList.add(inventoryTransaction);
                inventory.setTransactions(inventoryTransactionList);
            } else {
                inventory.getTransactions().add(inventoryTransaction);
            }

            // Insert Model Into Database
            inventoryRepository.saveAndFlush(inventory);

            return CompletableFuture.completedFuture(Result.success());
        } catch (Exception e) {
            return CompletableFuture.failedFuture( new Throwable("Failed to create inventory"));
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Result<List<InventoryDTO>>> getInventoriesByProductId(Long productId) {
            try {
                Result<ProductDTO> productResult = externalProductService.getProductById(productId);
                if (!productResult.isSuccess()) {
                    return CompletableFuture.completedFuture(Result.error(productResult.getErrorMessage()));
                }

                ProductDTO productDTO = productResult.getData();

                List<Inventory> inventories = inventoryRepository.findByProductId(productDTO.getId());
                List<InventoryDTO> inventoryDTOS = inventories.stream()
                        .map(inventory -> inventoryToReturnDTO(inventory, productDTO.getName()))
                        .collect(Collectors.toList());

                return CompletableFuture.completedFuture(Result.success(inventoryDTOS));
            } catch (Exception e) {
                return CompletableFuture.failedFuture( new Throwable("Failed to fetch inventory"));            }
    }

    /*
    public Result<List<InventoryReturnDTO>> getInventoryCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Inventory> inventories = inventoryRepository.findByCreatedAtBetween(startDate, endDate);
            List<InventoryReturnDTO> inventoryReturnDTOS = inventories.stream()
                    .map(inventory -> inventoryToReturnDTO(inventory, inventory.getProduct().getName()))
                    .collect(Collectors.toList());
            return Result.success(inventoryReturnDTOS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch inventory: " + e.getMessage());
        }
    }

    public Result<List<InventoryReturnDTO>> getInventoryCreatedAfter(LocalDateTime date) {
        try {
            List<Inventory> inventories = inventoryRepository.findByCreatedAtAfter(date);
            List<InventoryReturnDTO> inventoryReturnDTOS = inventories.stream()
                    .map(inventory -> inventoryToReturnDTO(inventory, inventory.getProduct().getName()))
                    .collect(Collectors.toList());
            return Result.success(inventoryReturnDTOS);
        } catch (Exception e) {
            return Result.error("Failed to fetch inventory: " + e.getMessage());
        }
    }

     */

    @Transactional
    public CompletableFuture<Result<Void>> deleteInventory(Long inventoryId) {
        try {
            Optional<Inventory> inventory = inventoryRepository.findById(inventoryId);
            if (inventory.isPresent()) {
                inventoryRepository.delete(inventory.get());
                return CompletableFuture.completedFuture(Result.success());
            } else {
                return CompletableFuture.completedFuture( Result.error("Inventory not found with ID: " + inventoryId));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete inventory: " + e.getMessage());
        }
    }
}

