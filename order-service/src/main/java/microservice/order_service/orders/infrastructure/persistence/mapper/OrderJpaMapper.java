package microservice.order_service.orders.infrastructure.persistence.mapper;

import libs_kernel.mapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.external.users.infrastructure.persistence.models.UserModel;
import microservice.order_service.orders.domain.models.Order;

import microservice.order_service.orders.domain.models.OrderItem;
import microservice.order_service.orders.domain.models.valueobjects.*;
import microservice.order_service.orders.infrastructure.persistence.models.DeliveryInfoModel;
import microservice.order_service.orders.infrastructure.persistence.models.OrderItemModel;
import microservice.order_service.orders.infrastructure.persistence.models.OrderModel;
import microservice.order_service.orders.infrastructure.persistence.models.PickupInfoModel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderJpaMapper implements ModelMapper<Order, OrderModel> {
    private final ModelMapper<OrderItem, OrderItemModel> itemMapper;

    @Override
    public OrderModel fromDomain(Order order) {
        if (order == null) return null;
        return OrderModel.builder()
                .id(order.getId().value() != null ? order.getId().value() : null)
                .deliveryMethod(order.getDeliveryMethod())
                .status(order.getStatus())
                .notes(order.getNotes())
                .items(itemMapper.fromDomains(order.getItems()))
                .user(order.getUserID() != null ? new UserModel(order.getUserID().value()) : null)
                .paymentID(order.getPaymentID() != null ? order.getPaymentID().value() : null)
                .taxFee(order.getTaxFee() != null ? order.getTaxFee().amount() : null)
                .deliveryInfo(DeliveryInfoModel.from(order.getDeliveryInfo()))
                .pickupInfo(PickupInfoModel.from(order.getPickupInfo()))
                .build();
    }

    @Override
    public Order toDomain(OrderModel model) {
        if (model == null) return null;
        System.out.println("Mapping OrderModel to Order domain: " + model.getCurrency());

        return Order.builder()
                .id(model.getId() != null ? OrderID.of(model.getId()) : null)
                .deliveryMethod(model.getDeliveryMethod())
                .status(model.getStatus())
                .notes(model.getNotes())
                .userID(model.getUserID() != null ? UserID.of(model.getUserID()) : null)
                .items(model.getItems() != null ? itemMapper.toDomains(model.getItems()) : List.of())
                .paymentID(model.getPaymentID() != null ? PaymentID.of(model.getPaymentID()) : null)
                .deliveryInfo(model.getDeliveryInfo() != null ? model.getDeliveryInfo().toDomain() : null)
                .pickupInfo(model.getPickupInfo() != null ? model.getPickupInfo().toDomain() : null)
                .taxFee(Money.of(model.getTaxFee(), model.getCurrency()))
                .orderCurrency(Currency.getInstance(model.getCurrency().getCode()))
                .orderTimestamps(new OrderTimestamps(
                        model.getCreatedAt(),
                        model.getUpdatedAt(),
                        model.getDeletedAt()))
                .build();
    }

    @Override
    public List<OrderModel> fromDomains(List<Order> orders) {
        if (orders == null || orders.isEmpty()) return List.of();

        return orders.stream()
                .map(this::fromDomain)
                .toList();
    }

    @Override
    public List<Order> toDomains(List<OrderModel> orderModels) {
        return orderModels.stream()
                .map(this::toDomain)
                .toList();
    }


    @Override
    public Page<Order> toDomainPage(Page<OrderModel> modelPage) {
        if (modelPage == null || modelPage.isEmpty()) return Page.empty();
        return modelPage.map(this::toDomain);
    }
}