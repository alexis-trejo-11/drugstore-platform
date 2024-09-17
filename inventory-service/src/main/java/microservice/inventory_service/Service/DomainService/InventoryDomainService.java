package microservice.inventory_service.Service.DomainService;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.TransactionType;
import microservice.inventory_service.Mapppers.InventoryTransactionMapper;
import microservice.inventory_service.Model.InventoryItem;
import microservice.inventory_service.Model.InventoryTransaction;
import microservice.inventory_service.Repository.InventoryTransactionRepository;
import microservice.inventory_service.Utils.*;
import microservice.inventory_service.Repository.InventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class InventoryDomainService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryTransactionMapper inventoryTransactionMapper;


    @Autowired
    public InventoryDomainService(InventoryItemRepository inventoryItemRepository,
                                  InventoryTransactionRepository inventoryTransactionRepository,
                                  InventoryTransactionMapper inventoryTransactionMapper) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.inventoryTransactionMapper = inventoryTransactionMapper;
    }

    public InventorySummary generateInventorySummary(LocalDateTime start, LocalDateTime end) {
        // Run SQL Queries Async
        CompletableFuture<List<InventoryItem>> inventoryItemsCreatedOfThisMonth = getInventoryItemsCreatedOfThisMonth(start, end);
        CompletableFuture<List<InventoryItem>> lowStockItems = getLowStockItems();
        CompletableFuture<List<ProductStockDTO>> productStockList = getProductStockSummary();
        CompletableFuture<Integer> countAllInventoryItems = countAllInventoryItems();

        CompletableFuture.allOf(inventoryItemsCreatedOfThisMonth, lowStockItems, productStockList).join();

        // Make Summary
        InventorySummary inventorySummary = new InventorySummary();
        inventorySummary.setProductStockDTO(productStockList.join());
        inventorySummary.setTotalItemEntries(countAllInventoryItems.join());
        inventorySummary.setLowStockItemsCount(lowStockItems.join().size());
        inventorySummary.setItemsRecentlyAdded(inventoryItemsCreatedOfThisMonth.join().size());
        inventorySummary.setTotalUniqueItems(productStockList.join().size());
        inventorySummary.setSummaryDateCreation(LocalDateTime.now());

        return inventorySummary;
    }

    @Transactional
    public TransactionSummary generateInventoryTransactionSummary(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        TransactionSummary transactionSummary = new TransactionSummary();
        setTransactionSummaryDateRange(transactionSummary, start, end);

        // Get Data async
        CompletableFuture<Page<InventoryTransactionDTO>> inventoryHistory = getInventoryHistory(start, end, pageable);
        List<CompletableFuture<TransactionTypeSummary>> transactionFutures = getTransactionTypeFutures(start, end);

        // Wait futures
        CompletableFuture.allOf(Stream.concat(Stream.of(inventoryHistory),
                        transactionFutures.stream()).toArray(CompletableFuture<?>[]::new)).join();

        // Set Data
        transactionSummary.setTransactionHistory(inventoryHistory.join());
        setTotalTransactionEntries(transactionSummary, transactionFutures);
        setTransactionTypeSummaries(transactionSummary, transactionFutures);

        return transactionSummary;
    }

    @Async("taskExecutor")
    private CompletableFuture<TransactionTypeSummary> getInventoryTransactionList(LocalDateTime start, LocalDateTime end, TransactionType transactionType) {
       return CompletableFuture.supplyAsync(() ->  {
           // Transaction Type
           List<InventoryTransaction> transactionsReceived = inventoryTransactionRepository.findByTransactionTypeAndDateRange(start, end, transactionType);
           TransactionTypeSummary transactionTypeSummary = new TransactionTypeSummary();
           transactionTypeSummary.setTransactionType(transactionType);
           transactionTypeSummary.setTotalTransactions(transactionsReceived.size());


            // Min Transaction
           InventoryTransaction minTransaction = transactionsReceived.stream().min(Comparator.comparingInt(InventoryTransaction::getQuantity)).orElse(null);
           if (minTransaction != null) {
               Long productId = minTransaction.getInventoryItem().getProductId();
               TransactionOperation minTransactionOperation = new TransactionOperation(productId, minTransaction.getQuantity());
               transactionTypeSummary.setMinProductEntry(minTransactionOperation);

           }

           // Max Transaction
           InventoryTransaction maxTransaction = transactionsReceived.stream().max(Comparator.comparingInt(InventoryTransaction::getQuantity)).orElse(null);
           if (maxTransaction != null) {
               Long maxProductId = maxTransaction.getInventoryItem().getProductId();
               TransactionOperation maxTransactionOperation = new TransactionOperation(maxProductId, maxTransaction.getQuantity());
               transactionTypeSummary.setMaxProductEntry(maxTransactionOperation);

           }

           // Average
           double average = transactionsReceived.stream()
                   .mapToDouble(InventoryTransaction::getQuantity)
                   .average()
                   .orElse(0);
           transactionTypeSummary.setAverageQuantityAdded(average);


            // Most Present Product
           // Create a Map with ProductID as Key and his Count as Value to help the search
           Map<Long, Long> productFrequencyMap = transactionsReceived.stream()
                   .collect(Collectors.groupingBy(
                           transaction -> transaction.getInventoryItem().getProductId(),
                           Collectors.counting()
                   ));

           // With map find the productId with more presence
           Long mostPresentProductId = productFrequencyMap.entrySet().stream()
                   .max(Map.Entry.comparingByValue())
                   .map(Map.Entry::getKey)
                   .orElse(null);

           // Knowing the productId calculate total quantity
           if (mostPresentProductId != null) {
               long totalQuantity = transactionsReceived.stream()
                       .filter(transaction -> transaction.getInventoryItem().getProductId().equals(mostPresentProductId))
                       .mapToLong(InventoryTransaction::getQuantity)
                       .sum();

               ProductStockDTO mostPresentProductStockDTO = new ProductStockDTO(mostPresentProductId, totalQuantity);
               transactionTypeSummary.setMostProductPresent(mostPresentProductStockDTO);
           }

           return transactionTypeSummary;
       });
    }

    @Async("taskExecutor")
    @Cacheable(value = "inventoryItems", key = "#start + '_' + #end")
    public CompletableFuture<List<InventoryItem>> getInventoryItemsCreatedOfThisMonth(LocalDateTime start, LocalDateTime end) {
        return CompletableFuture.completedFuture(inventoryItemRepository.findByCreatedAtBetween(start, end));
    }

    @Async("taskExecutor")
    @Cacheable("lowStockItems")
    public CompletableFuture<List<InventoryItem>> getLowStockItems() {
        return CompletableFuture.completedFuture(inventoryItemRepository.findByStockBelowTwentyPercent());
    }

    @Async("taskExecutor")
    @Cacheable("productStockSummary")
    public CompletableFuture<List<ProductStockDTO>> getProductStockSummary() {
        return CompletableFuture.completedFuture(inventoryItemRepository.findProductStockSummary());
    }

    public void updateProductTotalStock(InventoryItem inventoryItem, int quantityUpdate, TransactionType transactionType) {
        // Get Quantity
        int currentQuantity = inventoryItem.getQuantity();

        // Update Quantity By Action
        switch (transactionType) {
            case RECEIVED:
                inventoryItem.setQuantity(currentQuantity + quantityUpdate);
                break;
            case RETURNED, DAMAGED, EXPIRED, SOLD:
                inventoryItem.setQuantity(currentQuantity - quantityUpdate);
                break;
        }

        // Save Changes
        inventoryItem.setUpdatedAt(LocalDateTime.now());
        inventoryItemRepository.saveAndFlush(inventoryItem);
    }

    public ProductStockStatus generateProductStockStatus(List<InventoryItem> inventoryItems, Long productId) {
        ProductStockStatus productStockStatus = new ProductStockStatus();
        productStockStatus.setProductId(productId);

        int productTotalStock = inventoryItems.stream().mapToInt(InventoryItem::getQuantity).sum();
        int optimalStockLevelSum = inventoryItems.stream().mapToInt(InventoryItem::getOptimalStockLevel).sum();

        if (optimalStockLevelSum == 0 || productTotalStock == 0) {
            productStockStatus.setProductAvailable(false);
            productStockStatus.setTotalStock(0);
            productStockStatus.setStockStatus(ProductStockStatus.StockStatus.OUT_OF_STOCK);
            return productStockStatus;
        }

        productStockStatus.setProductAvailable(true);
        productStockStatus.setTotalStock(productTotalStock);
        productStockStatus.setCreatedAt(LocalDateTime.now());

        // Calculate Stock Percentage With Relation Of Optimal Stock (0.25 LOW STOCK -> 1.00 IN_STOCK -> 2.00 OVERSTOCKED )
        double stockPercentage = (double) productTotalStock / optimalStockLevelSum;

        // Asign Stock Status With a Map
        Map<Predicate<Double>, Runnable> actions = new LinkedHashMap<>();
        actions.put(t -> t <= 0.25, () -> productStockStatus.setStockStatus(ProductStockStatus.StockStatus.LOW_STOCK));
        actions.put(t -> t > 0.25 && t <= 1.0, () -> productStockStatus.setStockStatus(ProductStockStatus.StockStatus.IN_STOCK));
        actions.put(t -> t > 1.0 && t < 2.0, () -> productStockStatus.setStockStatus(ProductStockStatus.StockStatus.OVER_OPTIMAL_STOCK));
        actions.put(t -> t >= 2.0, () -> productStockStatus.setStockStatus(ProductStockStatus.StockStatus.OVERSTOCKED));

        actions.entrySet().stream()
                .filter(entry -> entry.getKey().test(stockPercentage))
                .findFirst()
                .ifPresent(entry -> entry.getValue().run());

        return productStockStatus;
    }

    public void updateStockForTransaction(InventoryItem inventoryItem, int currentQuantity, int requestedNewQuantity) {
        // If quantity is higher than existing quantity, increase the stock
        if (requestedNewQuantity > currentQuantity) {
            int quantityToAdd = requestedNewQuantity - currentQuantity;
            updateProductTotalStock(inventoryItem, quantityToAdd, TransactionType.RECEIVED);
        }
        // If quantity is less than existing quantity, decrease the stock
        else if (requestedNewQuantity < currentQuantity) {
            int quantityToSubtract = currentQuantity - requestedNewQuantity;
            updateProductTotalStock(inventoryItem, quantityToSubtract, TransactionType.RETURNED);
        }
    }



    // Helping Functions
    @Async("taskExecutor")
    private CompletableFuture<Page<InventoryTransactionDTO>> getInventoryHistory(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return CompletableFuture.supplyAsync(() ->  {
            // Inventory History
            Page<InventoryTransaction> inventoryTransactionPage = inventoryTransactionRepository.findByCreatedAtBetween(pageable, start, end);
            return inventoryTransactionPage.map(inventoryTransactionMapper::entityToDTO);
        });
    }

    @Async("taskExecutor")
    @Cacheable(value = "inventoryCounts", key = "'countAll'")
    private CompletableFuture<Integer> countAllInventoryItems() {
        return CompletableFuture.completedFuture(inventoryItemRepository.countAllEntries());
    }

    private void setTransactionSummaryDateRange(TransactionSummary transactionSummary, LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        String formattedStartDate = start.format(formatter);
        String formattedEndDate = end.format(formatter);
        transactionSummary.setTransactionSummaryDateRange("from " + formattedStartDate + " to " + formattedEndDate);
    }

    private List<CompletableFuture<TransactionTypeSummary>> getTransactionTypeFutures(LocalDateTime start, LocalDateTime end) {
        List<CompletableFuture<TransactionTypeSummary>> futures = new ArrayList<>();
        futures.add(getInventoryTransactionList(start, end, TransactionType.RECEIVED));
        futures.add(getInventoryTransactionList(start, end, TransactionType.RETURNED));
        futures.add(getInventoryTransactionList(start, end, TransactionType.SOLD));
        futures.add(getInventoryTransactionList(start, end, TransactionType.EXPIRED));
        futures.add(getInventoryTransactionList(start, end, TransactionType.DAMAGED));
        return futures;
    }

    private void setTotalTransactionEntries(TransactionSummary transactionSummary, List<CompletableFuture<TransactionTypeSummary>> transactionFutures) {
        int totalEntries = transactionFutures.stream()
                .mapToInt(future -> future.join().getTotalTransactions())
                .sum();
        transactionSummary.setTotalTransactionEntries(totalEntries);
    }

    private void setTransactionTypeSummaries(TransactionSummary transactionSummary, List<CompletableFuture<TransactionTypeSummary>> transactionFutures) {
        List<TransactionTypeSummary> transactionTypeSummaries = transactionFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        transactionSummary.setTransactionTypeSummary(transactionTypeSummaries);
    }
}

