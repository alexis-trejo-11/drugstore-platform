package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.TransactionType;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Employee.EmployeeFacadeService;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Products.ProductFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import jakarta.persistence.EntityNotFoundException;
import microservice.inventory_service.Mapppers.InventoryTransactionMapper;
import microservice.inventory_service.Model.InventoryItem;
import microservice.inventory_service.Model.InventoryTransaction;
import microservice.inventory_service.Repository.InventoryItemRepository;
import microservice.inventory_service.Repository.InventoryTransactionRepository;
import microservice.inventory_service.Service.DomainService.InventoryDomainService;
import microservice.inventory_service.Utils.TransactionSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.concurrent.CompletableFuture;

@Service
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionMapper inventoryTransactionMapper;
    private final ProductFacadeService productFacadeService;
    private final EmployeeFacadeService  employeeFacadeService;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryDomainService inventoryDomainService;

    @Autowired
    public InventoryTransactionServiceImpl(InventoryTransactionMapper inventoryTransactionMapper,
                                           ProductFacadeService productFacadeService,
                                           EmployeeFacadeService employeeFacadeService,
                                           InventoryTransactionRepository inventoryTransactionRepository,
                                           InventoryItemRepository inventoryItemRepository,
                                           InventoryDomainService inventoryDomainService) {
        this.inventoryTransactionMapper = inventoryTransactionMapper;
        this.productFacadeService = productFacadeService;
        this.employeeFacadeService = employeeFacadeService;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryDomainService = inventoryDomainService;
    }

    @Override
    @Cacheable(value = "transactionSummary", key = "'currentMonthSummary'")
    public TransactionSummary getTransactionSummary(Pageable pageable) {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(LocalDateTime.MAX.toLocalTime());

        return inventoryDomainService.generateInventoryTransactionSummary(startOfMonth, endOfMonth, pageable);
    }

    @Override
    public Page<InventoryTransactionDTO> getInventoryItemByEmployeeId(Long employeeId, Pageable pageable) {
        Page<InventoryTransaction> inventoryTransactionPage = inventoryTransactionRepository.findByEmployeeId(employeeId, pageable);
        return inventoryTransactionPage.map(inventoryTransactionMapper::entityToDTO);
    }

    @Override
    @Cacheable(value = "transactionsBySupplier", key = "#supplierId")
    public Page<InventoryTransactionDTO> getTransactionsBySupplierId(Long supplierId, Pageable pageable) {
        Page<InventoryTransaction> inventoryTransactionPage = inventoryTransactionRepository.findBySupplierId(supplierId,pageable);
        return inventoryTransactionPage.map(inventoryTransactionMapper::entityToDTO);
    }

    @Override
    @Cacheable(value = "transactionsByStatus", key = "#transactionType")
    public Page<InventoryTransactionDTO> getTransactionsByStatus(TransactionType transactionType, Pageable pageable) {
        Page<InventoryTransaction> inventoryTransactionPage = inventoryTransactionRepository.findByTransactionTypeSortedByDateDesc(transactionType, pageable);
        return inventoryTransactionPage.map(inventoryTransactionMapper::entityToDTO);
    }

    @Override
    @Cacheable(value = "nearToExpireTransactions", key = "'nextMonthTransactions'")
    public Page<InventoryTransactionDTO> getNearToExpire(Pageable pageable) {
        LocalDateTime nextMonth = LocalDateTime.now().plusMonths(1);

        // Look for Transaction That will expire after 30 days from now
        Page<InventoryTransaction> inventoryTransactionPage = inventoryTransactionRepository.findTransactionsNearToExpire(nextMonth, pageable);
        return inventoryTransactionPage.map(inventoryTransactionMapper::entityToDTO);
    }

    @Override
    public Page<InventoryTransactionDTO> getTransactionsOrderByDate(Pageable pageable) {
        Page<InventoryTransaction> inventoryTransactionPage =  inventoryTransactionRepository.findTransactionOrderByDateDesc(pageable);
        return inventoryTransactionPage.map(inventoryTransactionMapper::entityToDTO);
    }

    @Override
    @Cacheable(value = "transactionById", key = "#transactionId")
    public InventoryTransactionDTO getTransactionById(Long transactionId) {
        InventoryTransaction inventoryTransaction = inventoryTransactionRepository.findById(transactionId).orElse(null);
        return inventoryTransactionMapper.entityToDTO(inventoryTransaction);
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<Void>> validateTransactionRelationships(Long employeeId , Long supplierId) {
        CompletableFuture<Boolean> supplierValidationFuture = productFacadeService.validateExistingSupplier(supplierId);
        CompletableFuture<Boolean> employeeValidationFuture = employeeFacadeService.validateExistingEmployee(employeeId);

        return supplierValidationFuture.thenCombine(employeeValidationFuture, (isSupplierExisting, isEmployeeExisting) -> {
            if (!isSupplierExisting) {
                return Result.error("Supplier doesn't exist");
            } else if (!isEmployeeExisting) {
                return Result.error("Employee doesn't exist");
            }
            return Result.success();
        });
    }

    @Override
    @Transactional
    public InventoryTransactionDTO createTransaction(InventoryTransactionInsertDTO transactionInsertDTO) {
        // Create Inventory Transaction
        InventoryTransaction inventoryTransaction = inventoryTransactionMapper.insertDtoToEntity(transactionInsertDTO);

        InventoryItem inventoryItem = inventoryItemRepository.findById(transactionInsertDTO.getInventoryItemId())
                .orElseThrow(() -> new RuntimeException("Inventory Item not found"));

        inventoryTransaction.setInventoryItem(inventoryItem);

        inventoryTransactionRepository.saveAndFlush(inventoryTransaction);

        // Increase Or Decrease Product Total Stock
        inventoryDomainService.updateProductTotalStock(inventoryTransaction.getInventoryItem(), inventoryTransaction.getQuantity(), inventoryTransaction.getTransactionType());

        return inventoryTransactionMapper.entityToDTO(inventoryTransaction);
    }

    @Override
    @Transactional
    public InventoryTransactionDTO updateTransaction(Long transactionId, InventoryTransactionInsertDTO transactionInsertDTO) {
        InventoryTransaction updatedTransaction = inventoryTransactionMapper.updateDtoToEntity(transactionInsertDTO, transactionId);

        InventoryTransaction existingTransaction = inventoryTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory Transaction not found"));

        InventoryItem inventoryItem = existingTransaction.getInventoryItem();
        updatedTransaction.setInventoryItem(inventoryItem);

        // Not Updatable Stock Scenario
        if (transactionInsertDTO.getQuantity() == existingTransaction.getQuantity() &&
                transactionInsertDTO.getTransactionType() == existingTransaction.getTransactionType()) {
            inventoryTransactionRepository.saveAndFlush(updatedTransaction);
            return inventoryTransactionMapper.entityToDTO(updatedTransaction);
        }
        // Updatable Stock Scenario
        int requestedNewQuantity = transactionInsertDTO.getQuantity();
        int currentQuantity = existingTransaction.getQuantity();

        // Compare existing quantity and requested quantity and adjust the product total stock
        inventoryDomainService.updateStockForTransaction(inventoryItem, currentQuantity, requestedNewQuantity);
        inventoryTransactionRepository.saveAndFlush(updatedTransaction);

        return inventoryTransactionMapper.entityToDTO(updatedTransaction);
    }


    @Override
    @Transactional
    public void deleteTransaction(Long transactionId) {
        InventoryTransaction inventoryTransaction = inventoryTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Inventory Transaction not found"));

        InventoryItem inventoryItem = inventoryTransaction.getInventoryItem();
        inventoryDomainService.updateProductTotalStock(inventoryItem, inventoryTransaction.getQuantity(), TransactionType.RETURNED);
        inventoryTransactionRepository.delete(inventoryTransaction);
    }
}