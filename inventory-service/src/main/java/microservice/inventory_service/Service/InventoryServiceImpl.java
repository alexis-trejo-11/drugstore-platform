package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Employee.EmployeeFacadeService;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Products.ProductFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryDTO;
import microservice.inventory_service.Mapppers.DtoMappers;
import microservice.inventory_service.Model.Inventory;
import microservice.inventory_service.Repository.InventoryRepository;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import microservice.inventory_service.Service.DomainService.InventoryDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductFacadeService productFacadeService;
    private final EmployeeFacadeService employeeFacadeService;
    private final DtoMappers dtoMappers;
    private final InventoryDomainService inventoryDomainService;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                ProductFacadeService productFacadeService,
                                @Qualifier("employeeFacadeService") EmployeeFacadeService employeeFacadeService,
                                DtoMappers dtoMappers,
                                InventoryDomainService inventoryDomainService) {
        this.inventoryRepository = inventoryRepository;
        this.productFacadeService = productFacadeService;
        this.employeeFacadeService = employeeFacadeService;
        this.dtoMappers = dtoMappers;
        this.inventoryDomainService = inventoryDomainService;
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> createInventory(InventoryInsertDTO inventoryInsertDTO) {
        CompletableFuture<Result<ProductDTO>> productFuture = productFacadeService.getProductById(inventoryInsertDTO.getProductId());
        CompletableFuture<Result<EmployeeDTO>> employeeFuture = employeeFacadeService.getEmployeeById(inventoryInsertDTO.getInventoryTransactionInsertDTO().getEmployeeId());

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
    @Cacheable(value = "inventoriesByProductId", key = "#productId")
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
    @Transactional
    public boolean deleteInventory(Long inventoryId) {
        Optional<Inventory> inventory = inventoryRepository.findById(inventoryId);
        if (inventory.isEmpty()) {
            return false;
        }

        inventoryRepository.deleteById(inventoryId);
        return true;
    }


}
