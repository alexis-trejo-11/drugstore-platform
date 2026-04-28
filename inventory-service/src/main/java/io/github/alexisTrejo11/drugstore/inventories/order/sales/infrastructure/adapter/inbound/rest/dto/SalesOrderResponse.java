package io.github.alexisTrejo11.drugstore.inventories.order.sales.infrastructure.adapter.inbound.rest.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.valueobject.DeliveryMethod;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderStatus;

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
        ) {}
}
