package microservice.order_service.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import microservice.order_service.domain.models.Order;
import microservice.order_service.domain.models.enums.OrderStatus;
import microservice.order_service.domain.models.valueobjects.CustomerID;
import microservice.order_service.domain.models.valueobjects.OrderID;
import microservice.order_service.domain.ports.output.OrderRepository;
import microservice.order_service.infrastructure.persistence.mapper.OrderJpaMapper;
import microservice.order_service.infrastructure.persistence.models.OrderModel;
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
    public Page<Order> findByCustomerID(CustomerID customerId, Pageable pageable) {
     Page<OrderModel> orderModelPage = orderJpaRepository.findByCustomerId(customerId.toString(), pageable);
     return orderModelPage.map(mapper::toDomain);
    }

    @Override
    public Page<Order> findByCustomerIdAndOrderStatus(CustomerID customerId, OrderStatus status, Pageable pageable) {
        Page<OrderModel> orderModelPage = orderJpaRepository.findByCustomerIdAndStatus(
                customerId.toString(),
                status.toString(),
                pageable
        );

        return orderModelPage.map(mapper::toDomain);
    }

    @Override
    public Page<Order> findByCustomerIdAndRangeDate(
            CustomerID customerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        Page<OrderModel> orderModelPage = orderJpaRepository.findByCustomerIdAndDateRange(
                customerId.toString(),
                startDate,
                endDate,
                pageable
        );
        return orderModelPage.map(mapper::toDomain);
    }

    @Override
    public Optional<Order> findByCustomerIdAndId(CustomerID customerId, OrderID orderId) {
        Optional<OrderModel> orderModelOpt = orderJpaRepository.findByCustomerIdAndId(
                customerId.toString(),
                orderId.toString()
        );
        return orderModelOpt.map(mapper::toDomain);
    }

    @Override
    public List<Order> findRecentOrdersByCustomer(CustomerID customerId, int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        List<OrderModel> orderModels = orderJpaRepository.findRecentOrdersByCustomerId(
                customerId.toString(),
                pageable
        );

        return orderModels.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Long countByCustomerId(CustomerID customerId) {
        return orderJpaRepository.countByCustomerId(customerId.toString());
    }

    @Override
    public Long countByCustomerIdAndStatus(CustomerID customerId, OrderStatus status) {
        return orderJpaRepository.countByCustomerIdAndStatus(
                customerId.toString(),
                status.toString()
        );
    }

    @Override
    public Order save(Order order) {
     OrderModel orderModel = mapper.toModel(order);

     OrderModel savedModel = orderJpaRepository.save(orderModel);
     return mapper.toDomain(savedModel);
    }

    @Override
    public void softDelete(Order order) {
        OrderModel orderModel = mapper.toModel(order);
        orderModel.setDeletedAt(LocalDateTime.now());

        orderJpaRepository.save(orderModel);
    }

    @Override
    public void hardDelete(Order order) {
        OrderModel orderModel = mapper.toModel(order);
        orderJpaRepository.delete(orderModel);
    }

    @Override
    public Optional<Order> findById(OrderID orderId) {
        return orderJpaRepository.findById(orderId.value())
                .map(mapper::toDomain);

    }

    @Override
    public Optional<Order> findByCustomerIdAndOrderId(CustomerID customerId, OrderID orderId) {
        Optional<OrderModel> orderModelOpt = orderJpaRepository.findByCustomerIdAndId(
                customerId.toString(),
                orderId.toString()
        );
        return orderModelOpt.map(mapper::toDomain);
    }
}
