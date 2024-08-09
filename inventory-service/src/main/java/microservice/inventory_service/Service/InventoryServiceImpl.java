package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Inventory.InventoryTransactionDTO;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Employee.ExternalEmployeeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Products.ProductFacadeService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.DTOs.Inventory.InventoryInsertDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Inventory.InventoryDTO;
import microservice.inventory_service.Mapppers.DtoMappers;
import microservice.inventory_service.Mapppers.InventoryMapper;
import microservice.inventory_service.Model.Inventory;
import microservice.inventory_service.Model.InventoryTransaction;
import microservice.inventory_service.Repository.InventoryRepository;
import at.backend.drugstore.microservice.common_models.DTOs.Product.ProductDTO;
import microservice.inventory_service.Service.DomainService.InventoryDomainService;
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
    private final ProductFacadeService productFacadeService;
    private final ExternalEmployeeService externalEmployeeService;
    private final DtoMappers dtoMappers;
    private final InventoryDomainService inventoryDomainService;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                ProductFacadeService productFacadeService,
                                ExternalEmployeeService externalEmployeeService,
                                DtoMappers dtoMappers,
                                InventoryDomainService inventoryDomainService) {
        this.inventoryRepository = inventoryRepository;
        this.productFacadeService = productFacadeService;
        this.externalEmployeeService = externalEmployeeService;
        this.dtoMappers = dtoMappers;
        this.inventoryDomainService = inventoryDomainService;
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> createInventory(InventoryInsertDTO inventoryInsertDTO) {
        CompletableFuture<Result<ProductDTO>> productFuture = productFacadeService.getProductById(inventoryInsertDTO.getProductId());
        CompletableFuture<Result<EmployeeDTO>> employeeFuture = externalEmployeeService.findEmployeeById(inventoryInsertDTO.getInventoryTransactionInsertDTO().getEmployeeId());

        return CompletableFuture.allOf(productFuture, employeeFuture)
                .thenCompose(v -> {
                    Result<ProductDTO> productResult = productFuture.join();
                    Result<EmployeeDTO> employeeResult = employeeFuture.join();

                    if (!productResult.isSuccess()) {
                        return CompletableFuture.completedFuture(Result.error(productResult.getErrorMessage()));
                    }
                    ProductDTO productDTO = productResult.getData();

                    if (!employeeResult.isSuccess()) {
                        return CompletableFuture.completedFuture(Result.error(employeeResult.getErrorMessage()));
                    }
                    Long employeeId = employeeResult.getData().getId();

                    inventoryDomainService.processInventoryInsert(inventoryInsertDTO, productDTO, employeeId);
                    return CompletableFuture.completedFuture(Result.success());
                });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<InventoryDTO>> getInventoriesByProductId(Long productId) {
        return productFacadeService.getProductById(productId)
                .thenApplyAsync(productResult -> {
                    if (!productResult.isSuccess()) {
                        return Collections.emptyList();
                    }

                    ProductDTO productDTO = productResult.getData();
                    List<Inventory> inventories = inventoryRepository.findByProductId(productDTO.getId());

                    return inventories.stream()
                            .map(inventory ->  dtoMappers.mapInventoryToDTO(inventory, productDTO.getName()))
                            .collect(Collectors.toList());
                });
    }


    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Boolean> deleteInventory(Long inventoryId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Inventory> inventory = inventoryRepository.findById(inventoryId);
            if (inventory.isEmpty()) {
                return false;
            }

            inventoryRepository.deleteById(inventoryId);
            return true;
        });
    }


}
