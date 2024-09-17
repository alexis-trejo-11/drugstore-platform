package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Employee.EmployeeFacadeService;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Products.ProductFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.inventory_service.Repository.InventoryItemRepository;
import microservice.inventory_service.Repository.InventoryTransactionRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class InventoryValidationServiceImpl implements InventoryValidationService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ProductFacadeService productFacadeService;
    private final EmployeeFacadeService employeeFacadeService;

    public InventoryValidationServiceImpl(InventoryItemRepository inventoryItemRepository,
                                          InventoryTransactionRepository inventoryTransactionRepository,
                                          ProductFacadeService productFacadeService,
                                          EmployeeFacadeService employeeFacadeService) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.productFacadeService = productFacadeService;
        this.employeeFacadeService = employeeFacadeService;
    }

    @Override
    @Cacheable(value = "validateInventoryItems", key = "#inventoryItemId")
    public boolean validateExistingInventoryItem(Long inventoryItemId) {
        return inventoryItemRepository.findById(inventoryItemId).isPresent();
    }

    @Override
    @Cacheable(value = "validateInventoryTransactions", key = "#transactionId")
    public boolean validateExistingTransaction(Long transactionId) {
        return inventoryTransactionRepository.findById(transactionId).isPresent();
    }

    @Override
    @Cacheable(value = "validateProduct", key = "#productId")
    public boolean validateExistingProduct(Long productId) {
        CompletableFuture<Boolean> productValidationFuture = productFacadeService.validateExistingProduct(productId);
        return productValidationFuture.join();
    }

    @Override
    public boolean validateExistingEmployee(Long employeeId) {
        CompletableFuture<Boolean> productValidationFuture = employeeFacadeService.validateExistingEmployee(employeeId);
        return productValidationFuture.join();
    }

    @Override
    @Cacheable(value = "validateSupplier", key = "#supplierId")
    public boolean validateExistingSupplier(Long supplierId) {
        CompletableFuture<Boolean> supplierValidationFuture = productFacadeService.validateExistingSupplier(supplierId);
        return supplierValidationFuture.join();
    }

    @Override
    public Result<Void> validateInventoryItemRelationships(Long productId) {
        CompletableFuture<Boolean> productValidationFuture = productFacadeService.validateExistingProduct(productId);

        boolean isSupplierValidate = productValidationFuture.join();
        if (!isSupplierValidate) {
            return Result.error("Product doesn't exist");
        }

        return Result.success();
    }
}
