package microservice.order_service.orders.infrastructure.persistence.mapper;

import libs_kernel.mapper.ModelMapper;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.address.infrastructure.persistence.Model.DeliveryAddressModel;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.external.users.infrastructure.persistence.models.UserModel;
import microservice.order_service.orders.domain.models.Order;

import microservice.order_service.orders.domain.models.OrderItem;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.infrastructure.persistence.models.OrderItemModel;
import microservice.order_service.orders.infrastructure.persistence.models.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

@Component
public class OrderJpaMapper implements ModelMapper<Order, OrderModel> {
    public ModelMapper<OrderItem, OrderItemModel> itemMapper;
    private ModelMapper<DeliveryAddress,DeliveryAddressModel> addressMapper;
    private ModelMapper<User, UserModel> userMapper;

    @Override
    public OrderModel fromDomain(Order order) {
        return OrderModel.builder()
                .id(order.getId().value() != null ? order.getId().value() : null)
                .items(itemMapper.fromDomains(order.getItems()))
                .deliveryAddressModel(addressMapper.fromDomain(order.getDeliveryAddress()) != null ? addressMapper.fromDomain(order.getDeliveryAddress()) : null)
                .currency(order.getTotalAmount().currency().getCurrencyCode() != null ? order.getTotalAmount().currency().getCurrencyCode() : null)
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .user(order.getUser() != null ? userMapper.fromDomain(order.getUser()) : null)
                .createdAt(order.getCreatedAt() != null ? order.getCreatedAt() : LocalDateTime.now())
                .updatedAt(order.getUpdatedAt() != null ? order.getUpdatedAt() : LocalDateTime.now())
                .estimatedDeliveryDate(order.getEstimatedDeliveryDate() != null ? order.getEstimatedDeliveryDate() : null)
                .notes(order.getNotes() != null ? order.getNotes() : null)

                .build();
    }

    @Override
    public Order toDomain(OrderModel orderModel) {
        return Order.builder()
                .id(orderModel.getId() != null ? OrderID.of(orderModel.getId()) : null)
                .deliveryAddress(addressMapper.toDomain(orderModel.getDeliveryAddressModel()) != null ? addressMapper.toDomain(orderModel.getDeliveryAddressModel()) : null)
                .user(orderModel.getUser() != null ? userMapper.toDomain(orderModel.getUser()) : null)
                .items(orderModel.getItems() != null ? itemMapper.toDomains(orderModel.getItems()) : List.of())
                .status(orderModel.getStatus() != null ? OrderStatus.valueOf(orderModel.getStatus()) : null)
                .createdAt(orderModel.getCreatedAt() != null ? orderModel.getCreatedAt() : null)
                .updatedAt(orderModel.getUpdatedAt() != null ? orderModel.getUpdatedAt() : null)
                .estimatedDeliveryDate(orderModel.getEstimatedDeliveryDate() != null ? orderModel.getEstimatedDeliveryDate() : null)
                .notes(orderModel.getNotes() != null ? orderModel.getNotes() : null)
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