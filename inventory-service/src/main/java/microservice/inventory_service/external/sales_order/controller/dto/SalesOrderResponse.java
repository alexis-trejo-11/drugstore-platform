package microservice.inventory_service.external.sales_order.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.inventory_service.external.sales_order.model.DeliveryMethod;
import microservice.inventory_service.external.sales_order.model.OrderStatus;
import microservice.inventory_service.external.sales_order.service.dto.SalesOrderDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderResponse {
        private String id;
        private DeliveryMethod deliveryMethod;
        private OrderStatus status;
        private String notes;
        private String paymentId;
        private String deliveryInfoId;
        private String pickupInfoId;

        private List<SalesOrderItemResponse> items;

        public record SalesOrderItemResponse(
                String id,
                String productId,
                String productName,
                Integer quantity
        ) {
        }

        public static SalesOrderResponse fromDTO(SalesOrderDTO dto) {
            List<SalesOrderItemResponse> itemResponses = new ArrayList<>();
            for (var itemDto : dto.getItems()) {
                SalesOrderItemResponse itemResponse = new SalesOrderItemResponse(
                        itemDto.id(),
                        itemDto.productId(),
                        itemDto.productName(),
                        itemDto.quantity()
                );
                itemResponses.add(itemResponse);
            }

            return SalesOrderResponse.builder()
                    .id(dto.getId())
                    .deliveryMethod(dto.getDeliveryMethod())
                    .status(dto.getStatus())
                    .notes(dto.getNotes())
                    .paymentId(dto.getPaymentId())
                    .deliveryInfoId(dto.getDeliveryInfoId())
                    .pickupInfoId(dto.getPickupInfoId())
                    .items(itemResponses)
                    .build();
        }
}
