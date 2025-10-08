package microservice.order_service.orders.infrastructure.persistence.mapper;

import libs_kernel.mapper.ModelMapper;
import microservice.order_service.orders.domain.models.OrderItem;
import microservice.order_service.orders.domain.models.valueobjects.Money;
import microservice.order_service.orders.domain.models.valueobjects.ProductID;
import microservice.order_service.orders.infrastructure.persistence.models.OrderItemModel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderItemJpaMapper  implements ModelMapper<OrderItem, OrderItemModel> {
    @Override
    public OrderItemModel fromDomain(OrderItem item) {
        if (item == null) {
            return null;
        }
        return OrderItemModel.builder()
                .id(item.getId() != null ? item.getId() : null)
                .productId(item.getProductID() != null ? item.getProductID().value() : null)
                .productName(item.getProductName() != null ? item.getProductName() : null)
                .quantity(Math.max(item.getQuantity(), 0))
                .currency(item.getSubtotal() != null ? item.getSubtotal().currency().getCurrencyCode() : null)
                .subtotal(item.getSubtotal() != null ? item.getSubtotal().amount() : null)
                .isPrescriptionRequired(item.isPrescriptionRequired())
                .build();
    }

    @Override
    public OrderItem toDomain(OrderItemModel orderItemModel) {
        return OrderItem.builder()
                .id(orderItemModel.getId() != null ? orderItemModel.getId() : null)
                .productID(orderItemModel.getProductId() != null ? ProductID.of(orderItemModel.getProductId()) : null)
                .productName(orderItemModel.getProductName() != null ? orderItemModel.getProductName() : null)
                .subtotal(orderItemModel.getSubtotal() != null && orderItemModel.getCurrency() != null ?
                        Money.of(orderItemModel.getSubtotal(), java.util.Currency.getInstance(orderItemModel.getCurrency())) : null)
                .quantity(Math.max(orderItemModel.getQuantity(), 0))
                .prescriptionRequired(orderItemModel.isPrescriptionRequired())
                .build();
    }

    @Override
    public List<OrderItemModel> fromDomains(List<OrderItem> orderItems) {
        return orderItems.stream().map(this::fromDomain).toList();
    }

    @Override
    public List<OrderItem> toDomains(List<OrderItemModel> orderItemModels) {
        return orderItemModels.stream().map(this::toDomain).toList();
    }

    @Override
    public Page<OrderItem> toDomainPage(Page<OrderItemModel> orderItemModels) {
        return orderItemModels.map(this::toDomain);
    }
}
