package microservice.order_service.orders.infrastructure.persistence.mapper;

import libs_kernel.mapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.address.infrastructure.persistence.Model.DeliveryAddressModel;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.external.users.infrastructure.persistence.models.UserModel;
import microservice.order_service.orders.domain.models.Order;

import microservice.order_service.orders.domain.models.OrderItem;
import microservice.order_service.orders.domain.models.enums.DeliveryMethod;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import microservice.order_service.orders.domain.models.valueobjects.Money;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.PaymentID;
import microservice.order_service.orders.infrastructure.persistence.models.OrderItemModel;
import microservice.order_service.orders.infrastructure.persistence.models.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderJpaMapper implements ModelMapper<Order, OrderModel> {
    private final ModelMapper<OrderItem, OrderItemModel> itemMapper;
    private final ModelMapper<DeliveryAddress,DeliveryAddressModel> addressMapper;
    private final ModelMapper<User, UserModel> userMapper;

    @Override
    public OrderModel fromDomain(Order order) {
        return OrderModel.builder()
                .id(order.getId().value() != null ? order.getId().value() : null)
                .deliveryMethod(order.getDeliveryMethod() != null ? order.getDeliveryMethod().name() : null)
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .notes(order.getNotes() != null ? order.getNotes() : "")
                .items(itemMapper.fromDomains(order.getItems()))
                .deliveryAddressModel(order.getDeliveryAddress() != null ? new DeliveryAddressModel(order.getDeliveryAddress().getId().value()) : null)
                .user(order.getUser() != null ? new UserModel(order.getUser().getId().value()) : null)
                .paymentID(order.getPaymentID() != null ? order.getPaymentID().value() : null)

                .taxAmount(order.getTaxAmount() != null ? order.getTaxAmount().amount() : null)
                .shippingCost(order.getShippingCost() != null ? order.getShippingCost().amount() : null)

                .deliveryTrackingNumber(order.getDeliveryTrackingNumber() != null ? order.getDeliveryTrackingNumber() : null)
                .deliveryAttempt(order.getDeliveryAttempt() != null ? order.getDeliveryAttempt() : null)
                .daysSinceReadyForPickup( order.getDaysSinceReadyForPickup() != null ? order.getDaysSinceReadyForPickup() : null)

                .createdAt(order.getCreatedAt() != null ? order.getCreatedAt() : LocalDateTime.now())
                .updatedAt(order.getUpdatedAt() != null ? order.getUpdatedAt() : LocalDateTime.now())
                .estimatedDeliveryDate(order.getEstimatedDeliveryDate() != null ? order.getEstimatedDeliveryDate() : null)
                .build();
    }

    @Override
    public Order toDomain(OrderModel orderModel) {
        return Order.builder()
                .id(orderModel.getId() != null ? OrderID.of(orderModel.getId()) : null)
                .deliveryMethod(orderModel.getDeliveryMethod() != null ? DeliveryMethod.fromName(orderModel.getDeliveryMethod()) : null)
                .status(orderModel.getStatus() != null ? OrderStatus.fromName(orderModel.getStatus()) : null)

                .deliveryAddress(addressMapper.toDomain(orderModel.getDeliveryAddressModel()) != null ? addressMapper.toDomain(orderModel.getDeliveryAddressModel()) : null)
                .user(orderModel.getUser() != null ? userMapper.toDomain(orderModel.getUser()) : null)
                .items(orderModel.getItems() != null ? itemMapper.toDomains(orderModel.getItems()) : List.of())
                .paymentID(orderModel.getPaymentID() != null ? PaymentID.of(orderModel.getPaymentID()) : null)

                .shippingCost(Money.of(orderModel.getShippingCost(), Currency.getInstance("MXN")))
                .taxAmount(Money.of(orderModel.getTaxAmount(), Currency.getInstance("MXN")))

                .deliveryTrackingNumber(orderModel.getDeliveryTrackingNumber() != null ? orderModel.getDeliveryTrackingNumber() : null)
                .deliveryAttempt(orderModel.getDeliveryAttempt() != null ? orderModel.getDeliveryAttempt() : null)
                .daysSinceReadyForPickup(orderModel.getDaysSinceReadyForPickup() != null ? orderModel.getDaysSinceReadyForPickup() : null)

                .createdAt(orderModel.getCreatedAt() != null ? orderModel.getCreatedAt() : null)
                .updatedAt(orderModel.getUpdatedAt() != null ? orderModel.getUpdatedAt() : null)
                .estimatedDeliveryDate(orderModel.getEstimatedDeliveryDate() != null ? orderModel.getEstimatedDeliveryDate() : null)
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