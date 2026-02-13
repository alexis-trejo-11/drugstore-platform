package microservice.inventory_service.order.sales.infrastructure.adapter.outbound.repository;

import libs_kernel.mapper.JpaEntityMapper;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.ProductId;
import microservice.inventory_service.order.sales.core.domain.entity.SaleOrder;
import microservice.inventory_service.order.sales.core.domain.entity.SalesOrderItem;
import microservice.inventory_service.order.sales.core.domain.entity.valueobject.SaleOrderId;
import microservice.inventory_service.order.sales.core.domain.entity.valueobject.SalesOrderFullParams;
import microservice.inventory_service.order.sales.infrastructure.adapter.outbound.models.SaleOrderEntity;
import microservice.inventory_service.order.sales.infrastructure.adapter.outbound.models.SaleOrderItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SaleOrderJpaEntityMapper implements JpaEntityMapper<SaleOrderEntity, SaleOrder> {
    @Override
    public SaleOrderEntity fromDomain(SaleOrder saleOrder) {
        if (saleOrder == null) return null;

        SaleOrderEntity entity = new SaleOrderEntity();
        entity.setId(saleOrder.getId().value());
        entity.setPaymentId(saleOrder.getPaymentId());
        entity.setStatus(saleOrder.getStatus());
        entity.setNotes(saleOrder.getNotes());
        entity.setDeliveryMethod(saleOrder.getDeliveryMethod());
        entity.setCustomerUserId(saleOrder.getCustomerUserId());
        entity.setDeliveryInfoId(saleOrder.getDeliveryInfoId());
        entity.setPickupInfoId(saleOrder.getPickupInfoId());
        entity.setCreatedAt(saleOrder.getCreatedAt());
        entity.setUpdatedAt(saleOrder.getUpdatedAt());
        entity.setDeletedAt(saleOrder.getDeletedAt());
        entity.setVersion(saleOrder.getVersion());

        List<SaleOrderItemEntity> itemEntities = itemsToEntities(saleOrder.getItems(), entity);
        entity.setItems(itemEntities);

        return  entity;
    }

    private List<SaleOrderItemEntity> itemsToEntities(List<SalesOrderItem> items, SaleOrderEntity saleOrderEntity) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> {
                    SaleOrderItemEntity itemEntity = new SaleOrderItemEntity();
                    itemEntity.setId(item.getId());
                    itemEntity.setProductId(item.getProductId().value());
                    itemEntity.setOrderedQuantity(item.getOrderedQuantity());
                    itemEntity.setReceivedQuantity(item.getReceivedQuantity());
                    itemEntity.setProductName(item.getProductName());
                    itemEntity.setSaleOrder(saleOrderEntity);
                    itemEntity.setCreatedAt(item.getCreatedAt());
                    itemEntity.setUpdatedAt(item.getUpdatedAt());
                    itemEntity.setDeletedAt(item.getDeletedAt());
                    itemEntity.setVersion(item.getVersion());
                    return itemEntity;
                })
                .toList();
    }

    @Override
    public SaleOrder toDomain(SaleOrderEntity model) {
        if (model == null) return null;

        var reconstructorParams = SalesOrderFullParams.builder()
                .id(model.getId() != null ? new SaleOrderId(model.getId()) : null)
                .paymentId(model.getPaymentId())
                .status(model.getStatus())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .notes(model.getNotes()).deliveryMethod(model.getDeliveryMethod())
                .customerUserId(model.getCustomerUserId())
                .deliveryInfoId(model.getDeliveryInfoId())
                .pickupInfoId(model.getPickupInfoId())
                .items(entitiesToItems(model.getItems()))
                .build();

        return SaleOrder.reconstruct(reconstructorParams);
    }

    private List<SalesOrderItem> entitiesToItems(List<SaleOrderItemEntity> itemEntities) {
        if (itemEntities == null) {
            return List.of();
        }
        return itemEntities.stream()
                .map(itemEntity -> SalesOrderItem.reconstructor()
                        .id(itemEntity.getId())
                        .productId(new ProductId(itemEntity.getProductId()))
                        .productName(itemEntity.getProductName())
                        .orderedQuantity(itemEntity.getOrderedQuantity())
                        .receivedQuantity(itemEntity.getReceivedQuantity())
                        .createdAt(itemEntity.getCreatedAt())
                        .updatedAt(itemEntity.getUpdatedAt())
                        .deletedAt(itemEntity.getDeletedAt())
                        .version(itemEntity.getVersion())
                        .reconstruct()
                )
                .toList();
    }

    @Override
    public List<SaleOrderEntity> fromDomains(List<SaleOrder> saleOrders) {
        if (saleOrders == null) {
            return List.of();
        }
        return saleOrders.stream()
                .map(this::fromDomain)
                .toList();
    }

    @Override
    public List<SaleOrder> toDomains(List<SaleOrderEntity> saleOrderEntities) {
        if (saleOrderEntities == null) return List.of();

        return saleOrderEntities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<SaleOrder> toDomainPage(Page<SaleOrderEntity> modelPage) {
        if (modelPage == null) return Page.empty();
        return modelPage.map(this::toDomain);
    }
}
