package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.service;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.batch.domain.entity.valueobject.BatchId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject.ProductId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.InventoryNotFoundException;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.service.InventoryAllocationService;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port.InventoryRepository;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.entity.StockReservation;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.entity.StockReservationItem;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.ReservationId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.port.output.StockReservationRepository;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InventoryStockService {
    private final InventoryRepository inventoryRepository;
    private final InventoryAllocationService allocationService;
    private final StockReservationRepository reservationRepository;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(InventoryStockService.class);

    @Autowired
    public InventoryStockService(InventoryRepository inventoryRepository,
                                 InventoryAllocationService allocationService,
                                 StockReservationRepository reservationRepository) {
        this.inventoryRepository = inventoryRepository;
        this.allocationService = allocationService;
        this.reservationRepository = reservationRepository;
    }

    public boolean isStockAvailable(Map<ProductId, Integer> productQuantityMap) {
        logger.info("Checking stock availability for products: {}", productQuantityMap.keySet());

        Set<ProductId> productIds = productQuantityMap.keySet();
        try {
            List<Inventory> productInventories = inventoryRepository.findByProductIdInOrThrow(productIds);

            if (productInventories.size() != productIds.size()) {
                throw new IllegalStateException("Inconsistency in inventory retrieval");
            }

            for (var inventory : productInventories) {
                Integer quantity = productQuantityMap.get(inventory.getProductId());
                if (!inventory.hasAvailableStock(quantity)) {
                    return false;
                }
                logger.info("Product ID {} has sufficient stock: requested {}, available {}",
                            inventory.getProductId(), quantity, inventory.getAvailableQuantity());
            }

            logger.info("All products have sufficient stock.");
            return true;


        } catch (InventoryNotFoundException e) {
            logger.warn("Didn't find inventory for all product IDs: {}", e.getMessage());
            return false;
        }
    }


    public ReservationId reserveStock(Map<ProductId, Integer> productQuantityMap, OrderReference orderReference, String reservationReason) {
        logger.info("Reserving stock for order {}: {}", orderReference, productQuantityMap);

        Set<ProductId> productIds = productQuantityMap.keySet();
        var orderReservations = StockReservation.create(orderReference);
        logger.info("Created order stock reservation with ID: {}", orderReservations.getId());

        List<Inventory> productInventories = inventoryRepository.findByProductIdInOrThrow(productIds);
        productInventories.forEach(inventory -> {
            Integer quantityToReserve = productQuantityMap.get(inventory.getProductId());

            inventory.reserveStock(quantityToReserve);
            var assignedBatchId = allocationService.assingCompatibleBatch(inventory, quantityToReserve);

            var reservationItem  = StockReservationItem.create(
                    inventory.getId(),
                    assignedBatchId,
                    quantityToReserve,
                    reservationReason
            );
            orderReservations.addStockReservation(reservationItem);
        });

        logger.info("Reserving stock in inventories and updating order reservations.");

        inventoryRepository.bulkSave(productInventories);
        logger.info("Updated inventories saved.");

        var savedOrderReservations= reservationRepository.save(orderReservations);
        logger.info("Order stock reservations saved with ID: {}", orderReservations.getId());

        logger.info("Stock reservation process completed for order {}", orderReference);
        return savedOrderReservations.getId();
    }

    public void releaseStockByOrder(OrderReference orderReference) {
        logger.info("Releasing stock reservations for order {}", orderReference);
        StockReservation stockReservation = reservationRepository.findByOrderReference(orderReference)
                .orElseThrow(() -> new IllegalStateException("No reservations found for order"));


        logger.info("Found order stock reservations with ID: {}", stockReservation.getId());
        List<Inventory> inventories = inventoryRepository.findByIdIn(stockReservation.getInventoryIds());

        // WARNING: Batch Could be expired or inactive at this point
        inventories.forEach(inventory -> {
            if (stockReservation.hasReservationFor(inventory.getId())) {
                // Release reservation in inventory
                Integer reservedQuantity = stockReservation.getReservedQuantityFor(inventory.getId());
                inventory.releaseReservation(reservedQuantity);


                // Return quantity to batch via allocation service
                BatchId batchId = stockReservation.getAssociatedBatchIdFor(inventory.getId());
                allocationService.returnBatchQuantity(batchId, reservedQuantity);
            }
        });

        logger.info("Releasing all reservations for order {}", orderReference);
        stockReservation.releaseAll();

        logger.info("Saving updated reservations and inventories.");
        reservationRepository.save(stockReservation);

        logger.info("Updated reservations saved.");
        inventoryRepository.bulkSave(inventories);

        logger.info("Inventories updated after releasing reservations for order {}", orderReference);
    }


}
