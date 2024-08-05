package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryTransactionDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Employee.ExternalEmployeeService;
import at.backend.drugstore.microservice.common_models.ExternalService.Products.ExternalProductService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryDTO;
import microservice.inventory_service.Mapppers.InventoryMapper;
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

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ExternalProductService externalProductService;
    private final ExternalEmployeeService externalEmployeeService;
    private final InventoryMapper inventoryMapper;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository, ExternalProductService externalProductService, ExternalEmployeeService externalEmployeeService, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.externalProductService = externalProductService;
        this.externalEmployeeService = externalEmployeeService;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> createInventory(InventoryInsertDTO inventoryInsertDTO) {
        return externalProductService.getProductById(inventoryInsertDTO.getProductId())
                .thenCombineAsync(externalEmployeeService.findEmployeeById(inventoryInsertDTO.getInventoryTransactionInsertDTO().getEmployeeId()),
                        (productResult, employeeResult) -> {
                            if (!productResult.isSuccess()) {
                                return Result.error(productResult.getErrorMessage());
                            }

                            if (!employeeResult.isSuccess()) {
                                return Result.error(employeeResult.getErrorMessage());
                            }

                            Inventory inventory = new Inventory(inventoryInsertDTO);
                            ProductDTO productDTO = productResult.getData();
                            inventory.setProductId(productDTO.getId());

                            InventoryTransaction inventoryTransaction = new InventoryTransaction(inventoryInsertDTO.getInventoryTransactionInsertDTO(), employeeResult.getData().getId());
                            inventoryTransaction.setInventory(inventory);

                            if (inventory.getTransactions() == null) {
                                List<InventoryTransaction> inventoryTransactionList = new ArrayList<>();
                                inventoryTransactionList.add(inventoryTransaction);
                                inventory.setTransactions(inventoryTransactionList);
                            } else {
                                inventory.getTransactions().add(inventoryTransaction);
                            }

                            inventoryRepository.saveAndFlush(inventory);
                            return Result.success();
                        });
    }


    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<InventoryDTO>> getInventoriesByProductId(Long productId) {
        return externalProductService.getProductById(productId)
                .thenApplyAsync(productResult -> {
                    if (!productResult.isSuccess()) {
                        return Collections.emptyList();
                    }

                    ProductDTO productDTO = productResult.getData();
                    List<Inventory> inventories = inventoryRepository.findByProductId(productDTO.getId());

                    return inventories.stream()
                            .map(inventory -> mapInventoryToDTO(inventory, productDTO.getName()))
                            .collect(Collectors.toList());
                });
    }


    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Boolean> deleteInventory(Long inventoryId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Inventory> inventory = inventoryRepository.findById(inventoryId);
            if (!inventory.isPresent()) {
                return false;
            }

            inventoryRepository.deleteById(inventoryId);
            return true;
        });
    }

    private InventoryDTO mapInventoryToDTO(Inventory inventory, String productName) {
        InventoryDTO inventoryDTO = inventoryMapper.inventoryToDTO(inventory, productName);
        List<InventoryTransactionDTO> inventoryTransactionDTOS = inventory.getTransactions().stream()
                .map(inventoryMapper::inventoryTransactionToDTO)
                .collect(Collectors.toList());
        inventoryDTO.setInventoryTransactionDTOS(inventoryTransactionDTOS);
        return inventoryDTO;
    }
}
