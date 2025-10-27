package microservice.inventory_service.external.sales_order.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.external.sales_order.model.DeliveryMethod;
import microservice.inventory_service.external.sales_order.model.OrderStatus;
import microservice.inventory_service.external.sales_order.model.SaleOrderEntity;
import microservice.inventory_service.external.sales_order.model.SaleOrderItemEntity;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderDTO {
    private String id;
    private DeliveryMethod deliveryMethod;
    private OrderStatus status;
    private String notes;
    private String paymentId;
    private String deliveryInfoId;
    private String pickupInfoId;
    private List<SaleOrderItemDTO> items;

    public void validate() {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Payment ID cannot be null or blank");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Order must contain at least one item");
        if (deliveryMethod == null) throw new IllegalArgumentException("Delivery method cannot be null");
        if (status == null) throw new IllegalArgumentException("Order status cannot be null");

        items.forEach(SaleOrderItemDTO::validate);
    }

    public static SalesOrderDTO fromEntity(SaleOrderEntity entity) {
        List<SaleOrderItemDTO> itemDTOs = new ArrayList<>();
        if (entity.getItems() != null) {
            for (var itemEntity : entity.getItems()) {
                SaleOrderItemDTO itemDTO = new SaleOrderItemDTO(
                        itemEntity.getId(),
                        itemEntity.getProductId(),
                        itemEntity.getProductName(),
                        itemEntity.getQuantity()
                );
                itemDTOs.add(itemDTO);
            }
        }
        return SalesOrderDTO.builder()
                .id(entity.getId())
                .deliveryMethod(entity.getDeliveryMethod())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .paymentId(entity.getPaymentId())
                .deliveryInfoId(entity.getDeliveryInfoId())
                .pickupInfoId(entity.getPickupInfoId())
                .items(itemDTOs)
                .build();
    }

    public SaleOrderEntity toEntity() {
        List<SaleOrderItemEntity> itemEntities = new ArrayList<>();
        if (items != null) {
            for (var item : items) {
                var itemEntity = item.toEntity(id);
                itemEntities.add(itemEntity);
            }
        }
        return SaleOrderEntity.builder()
                .id(id)
                .deliveryMethod(deliveryMethod)
                .status(status)
                .notes(notes)
                .paymentId(paymentId)
                .deliveryInfoId(deliveryInfoId)
                .pickupInfoId(pickupInfoId)
                .items(itemEntities)
                .build();
    }
}


