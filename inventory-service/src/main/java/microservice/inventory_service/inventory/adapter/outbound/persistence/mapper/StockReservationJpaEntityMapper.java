package microservice.inventory_service.inventory.adapter.outbound.persistence.mapper;

import libs_kernel.mapper.JpaEntityMapper;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.inventory.adapter.outbound.persistence.model.InventoryBatchEntity;
import microservice.inventory_service.inventory.adapter.outbound.persistence.model.InventoryEntity;
import microservice.inventory_service.inventory.adapter.outbound.persistence.model.ReservationItemEntity;
import microservice.inventory_service.inventory.adapter.outbound.persistence.model.StockReservationsEntity;
import microservice.inventory_service.inventory.core.batch.domain.entity.valueobject.BatchId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.stock.domain.entity.StockReservation;
import microservice.inventory_service.inventory.core.stock.domain.entity.StockReservationItem;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationId;
import microservice.inventory_service.shared.domain.order.OrderReference;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class StockReservationJpaEntityMapper implements JpaEntityMapper<StockReservationsEntity, StockReservation> {
    @Override
    public StockReservationsEntity fromDomain(StockReservation reservation) {
        if (reservation == null) return null;

        var stockReservations = new StockReservationsEntity();
        stockReservations.setId(reservation.getId() != null ? reservation.getId().value() : null);
        stockReservations.setOrderId(reservation.getOrderReference().orderId());
        stockReservations.setOrderType(reservation.getOrderReference().type());
        stockReservations.setStatus(reservation.getStatus());
        stockReservations.setExpirationTime(reservation.getExpirationTime());
        stockReservations.setCreatedAt(reservation.getCreatedAt());
        stockReservations.setUpdatedAt(reservation.getUpdatedAt());
        stockReservations.setDeletedAt(reservation.getDeletedAt());
        stockReservations.setVersion(reservation.getVersion());

        List<ReservationItemEntity> items = new ArrayList<>();
        if (reservation.getReservations() != null) {
            var reservationItems = reservation.getReservations().values();
            for (var item : reservationItems) {
                var reservationItem = new ReservationItemEntity();
                reservationItem.setId(item.getId() != null ? item.getId().value() : null);
                reservationItem.setBatch(new InventoryBatchEntity(item.getAssociatedBatchId() != null ? item.getAssociatedBatchId().value() : null));
                reservationItem.setInventory(new InventoryEntity(item.getInventoryId() != null ? item.getInventoryId().value() : null));
                reservationItem.setQuantity(item.getQuantity());
                reservationItem.setReason(item.getReason());
                reservationItem.setCreatedAt(item.getCreatedAt());
                reservationItem.setUpdatedAt(item.getUpdatedAt());
                reservationItem.setDeletedAt(item.getDeletedAt());
                reservationItem.setVersion(item.getVersion());

                reservationItem.setReservation(stockReservations);

                items.add(reservationItem);
            }
        } else {
            log.warn("StockReservation with id:{} has null reservations map.", reservation.getId() != null ? reservation.getId().value() : "null");
        }

        stockReservations.setItems(items);
        return stockReservations;
    }

    @Override
    public StockReservation toDomain(StockReservationsEntity model) {
        if (model == null) return null;
        return StockReservation.reconstructor()
                .id(model.getId() != null ? new ReservationId(model.getId()) : null)
                .orderReference(OrderReference.from(model.getOrderType(), model.getOrderId()))
                .reservations(convertItemsToMap(model.getItems()))
                .status(model.getStatus())
                .expirationTime(model.getExpirationTime())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .reconstruct();

    }

    private Map<InventoryId, StockReservationItem> convertItemsToMap(List<ReservationItemEntity> items) {
        Map<InventoryId, StockReservationItem> reservationsMap = new java.util.HashMap<>();

        if (items != null) {
            for (var item : items) {
                InventoryId inventoryId = item.getInventory() != null ? new InventoryId(item.getInventory().getId()) : null;
                if (inventoryId == null) {
                    log.warn("Skipping reservation item with null inventoryId of itemId:{}", item.getId());
                    continue; // Skip items with null inventoryId
                }

                var reservationItem = StockReservationItem.reconstructor()
                        .id(item.getId() != null ? new ReservationId(item.getId()) : null)
                        .inventoryId(inventoryId)
                        .associatedBatchId(item.getBatch() != null ? new BatchId(item.getBatch().getId()) : null)
                        .quantity(item.getQuantity())
                        .reason(item.getReason())
                        .reconstruct();

                reservationsMap.put(inventoryId, reservationItem);
            }
        }
        return reservationsMap;
    }

    @Override
    public List<StockReservationsEntity> fromDomains(List<StockReservation> stockReservations) {
        if (stockReservations == null) return List.of();
        return stockReservations.stream()
                .map(this::fromDomain)
                .toList();

    }

    @Override
    public List<StockReservation> toDomains(List<StockReservationsEntity> stockReservationsEntities) {
        if (stockReservationsEntities == null) return List.of();
        return stockReservationsEntities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<StockReservation> toDomainPage(Page<StockReservationsEntity> modelPage) {
        return modelPage.map(this::toDomain);
    }
}
