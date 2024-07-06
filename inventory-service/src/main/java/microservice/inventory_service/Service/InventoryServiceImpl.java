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

    @Async
    @Transactional
    public Result<Void> createInventory(InventoryInsertDTO inventoryInsertDTO) {
        Inventory inventory = new Inventory(inventoryInsertDTO);

        Result<ProductDTO> productResult = externalProductService.getProductById(inventoryInsertDTO.getProductId());
        if (!productResult.isSuccess()) {
            return Result.error(productResult.getErrorMessage());
        }

        Result<EmployeeDTO> employeeResult = externalEmployeeService.findEmployeeById(inventoryInsertDTO.getInventoryTransactionInsertDTO().getEmployeeId());
        if (!productResult.isSuccess()) {
            return Result.error(employeeResult.getErrorMessage());
        }

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
    }

    @Async
    @Transactional
    public List<InventoryDTO> getInventoriesByProductId(Long productId) {
        Result<ProductDTO> productResult = externalProductService.getProductById(productId);
        if (!productResult.isSuccess()) {
            return null;
        }

        ProductDTO productDTO = productResult.getData();

        List<Inventory> inventories = inventoryRepository.findByProductId(productDTO.getId());

        return inventories.stream()
                .map(inventory -> mapInventoryToDTO(inventory, productDTO.getName()))
                .collect(Collectors.toList());
    }

    private InventoryDTO mapInventoryToDTO(Inventory inventory, String productName) {
        InventoryDTO inventoryDTO = inventoryMapper.inventoryToDTO(inventory, productName);
        List<InventoryTransactionDTO> inventoryTransactionDTOS = inventory.getTransactions().stream()
                .map(inventoryMapper::inventoryTransactionToDTO)
                .collect(Collectors.toList());
        inventoryDTO.setInventoryTransactionDTOS(inventoryTransactionDTOS);
        return inventoryDTO;
    }


    /*
    public Result<List<InventoryDTO>> getInventoryCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Inventory> inventories = inventoryRepository.findByCreatedAtBetween(startDate, endDate);
            List<InventoryDTO> inventoryReturnDTOS = inventories.stream()
                    .map(inventory -> inventoryToReturnDTO(inventory, inventory.getProduct().getName()))
                    .collect(Collectors.toList());
            return Result.success(inventoryReturnDTOS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch inventory: " + e.getMessage());
        }
    }

    public Result<List<InventoryDTO>> getInventoryCreatedAfter(LocalDateTime date) {
        try {
            List<Inventory> inventories = inventoryRepository.findByCreatedAtAfter(date);
            List<InventoryDTO> inventoryReturnDTOS = inventories.stream()
                    .map(inventory -> inventoryToReturnDTO(inventory, inventory.getProduct().getName()))
                    .collect(Collectors.toList());
            return Result.success(inventoryReturnDTOS);
        } catch (Exception e) {
            return Result.error("Failed to fetch inventory: " + e.getMessage());
        }
    }
     */

    @Async
    @Transactional
    public boolean deleteInventory(Long inventoryId) {
        Optional<Inventory> inventory = inventoryRepository.findById(inventoryId);
        if (inventory.isPresent()) {
            return false;
        }

        inventoryRepository.findById(inventoryId);

        return  true;
    }
}

