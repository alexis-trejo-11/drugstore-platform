package microservice.inventory_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.TransactionType;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.inventory_service.Utils.TransactionSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.CompletableFuture;

public interface InventoryTransactionService {
    InventoryTransactionDTO getTransactionById(Long transactionId);
    CompletableFuture<Result<Void>> validateTransactionRelationships(Long employeeId , Long supplierId);
    TransactionSummary getTransactionSummary(Pageable pageable);
    Page<InventoryTransactionDTO> getTransactionsBySupplierId(Long supplierId, Pageable pageable);
    Page<InventoryTransactionDTO> getTransactionsByStatus(TransactionType transactionType, Pageable pageable);
    Page<InventoryTransactionDTO> getNearToExpire(Pageable pageable);
    Page<InventoryTransactionDTO> getTransactionsOrderByDate(Pageable pageable);
    Page<InventoryTransactionDTO> getInventoryItemByEmployeeId(Long employeeId, Pageable pageable);

    InventoryTransactionDTO createTransaction(InventoryTransactionInsertDTO transactionInsertDTO);
    InventoryTransactionDTO updateTransaction(Long transactionId, InventoryTransactionInsertDTO transactionInsertDTO);
    void deleteTransaction(Long transactionId);
}
