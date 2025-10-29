package microservice.inventory_service.order.sales.core.application.query;

import microservice.inventory_service.order.sales.core.domain.entity.valueobject.SaleOrderId;
import microservice.inventory_service.shared.domain.order.OrderStatus;
import org.springframework.data.domain.Pageable;

public record GetSaleOrdersById(
        SaleOrderId orderId
) {

    public static GetSaleOrdersById of(String id) {
        return new GetSaleOrdersById(new SaleOrderId(id));
    }
}
