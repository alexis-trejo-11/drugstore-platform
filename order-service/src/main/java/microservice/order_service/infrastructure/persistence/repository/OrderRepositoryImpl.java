package microservice.order_service.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.enums.OrderStatus;
import microservice.order_service.domain.models.valueobjects.CustomerId;
import microservice.order_service.domain.models.valueobjects.OrderId;
import microservice.order_service.domain.ports.output.OrderRepository;
import microservice.order_service.infrastructure.persistence.mapper.OrderJpaMapper;
import microservice.order_service.infrastructure.persistence.models.OrderModel;
import microservice.order_service.infrastructure.persistence.models.OrderStatusModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final JpaOrderRepository orderJpaRepository;
    private final OrderJpaMapper mapper;

    @Override
    public Page<Order> findByCustomerID(CustomerId customerId, Pageable pageable) {
     Page<OrderModel> orderModelPage = orderJpaRepository.findByCustomerId(customerId.value(), pageable);
     return orderModelPage.map(mapper::toDomain);
    }

    @Override
    public Page<Order> findByCustomerIdAndOrderStatus(CustomerId customerId, OrderStatus status, Pageable pageable) {
        OrderStatusModel statusModel = mapper.mapOrderStatusToEntity(status);
        Page<OrderModel> orderModelPage = orderJpaRepository.findByCustomerIdAndStatus(
                customerId.value(),
                statusModel,
                pageable
        );

        return orderModelPage.map(mapper::toDomain);
    }

    @Override
    public Page<Order> findByCustomerIdAndRangeDate(
            CustomerId customerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        Page<OrderModel> orderModelPage = orderJpaRepository.findByCustomerIdAndDateRange(
                customerId.value(),
                startDate,
                endDate,
                pageable
        );
        return orderModelPage.map(mapper::toDomain);
    }

    @Override
    public Optional<Order> findByCustomerIdAndId(CustomerId customerId, OrderId orderId) {
        Optional<OrderModel> orderModelOpt = orderJpaRepository.findByCustomerIdAndId(
                customerId.value(),
                orderId.value()
        );
        return orderModelOpt.map(mapper::toDomain);
    }

    @Override
    public List<Order> findRecentOrdersByCustomer(CustomerId customerId, int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        List<OrderModel> orderModels = orderJpaRepository.findRecentOrdersByCustomerId(
                customerId.value(),
                pageable
        );

        return orderModels.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Long countByCustomerId(CustomerId customerId) {
        return orderJpaRepository.countByCustomerId(customerId.value());
    }

    @Override
    public Long countByCustomerIdAndStatus(CustomerId customerId, OrderStatus status) {
        OrderStatusModel statusModel = mapper.mapOrderStatusToEntity(status);
        return orderJpaRepository.countByCustomerIdAndStatus(
                customerId.value(),
                statusModel
        );
    }

    @Override
    public Order save(Order order) {
     OrderModel orderModel = mapper.toModel(order);

     OrderModel savedModel = orderJpaRepository.save(orderModel);
     return mapper.toDomain(savedModel);
    }

    @Override
    public Optional<Order> findByCustomerIdAndOrderId(CustomerId customerId, OrderId orderId) {
        Optional<OrderModel> orderModelOpt = orderJpaRepository.findByCustomerIdAndId(
                customerId.value(),
                orderId.value()
        );
        return orderModelOpt.map(mapper::toDomain);
    }
}
