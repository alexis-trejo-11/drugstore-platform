package microservice.order_service.orders.infrastructure.persistence.repository;

import libs_kernel.mapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.enums.OrderStatus;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.ports.output.OrderRepository;
import microservice.order_service.orders.infrastructure.persistence.models.OrderModel;
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
    private final ModelMapper<Order, OrderModel> mapper;

    @Override
    public Page<Order> findByUserID(UserID customerID, Pageable pageable) {
     Page<OrderModel> orderModelPage = orderJpaRepository.findByUserId(customerID.toString(), pageable);
     return orderModelPage.map(mapper::toDomain);
    }

    @Override
    public Page<Order> findByUserIDAndOrderStatus(UserID customerID, OrderStatus status, Pageable pageable) {
        Page<OrderModel> orderModelPage = orderJpaRepository.findByUserIdAndStatus(
                customerID.toString(),
                status.toString(),
                pageable
        );

        return orderModelPage.map(mapper::toDomain);
    }

    @Override
    public Page<Order> findByUserIDAndRangeDate(
            UserID customerID,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    ) {
        Page<OrderModel> orderModelPage = orderJpaRepository.findByUserIdAndDateRange(
                customerID.toString(),
                startDate,
                endDate,
                pageable
        );
        return orderModelPage.map(mapper::toDomain);
    }

    @Override
    public Optional<Order> findByUserIDAndID(UserID customerID, OrderID orderID) {
        Optional<OrderModel> orderModelOpt = orderJpaRepository.findByUserIdAndId(
                customerID.toString(),
                orderID.toString()
        );
        return orderModelOpt.map(mapper::toDomain);
    }

    @Override
    public List<Order> findRecentOrdersByUser(UserID customerID, int limit) {
        Pageable pageable = Pageable.ofSize(limit);
        List<OrderModel> orderModels = orderJpaRepository.findRecentOrdersByUserId(
                customerID.toString(),
                pageable
        );

        return orderModels.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsAnyByAddressIDAndOngoingStatus(AddressID addressID) {
        List<OrderStatus> ongoingStatuses = OrderStatus.getActiveStatuses();

        return orderJpaRepository.existsByAddressIdAndStatusInNative(
                addressID.value(),
                ongoingStatuses.stream().map(OrderStatus::name).toList()
        );
    }

    @Override
    public Long countByUserID(UserID customerID) {
        return orderJpaRepository.countByUserId(customerID.toString());
    }

    @Override
    public Long countByUserIDAndStatus(UserID customerID, OrderStatus status) {
        return orderJpaRepository.countByUserIdAndStatus(
                customerID.toString(),
                status.toString()
        );
    }

    @Override
    public Order save(Order order) {
     OrderModel orderModel = mapper.fromDomain(order);

     OrderModel savedModel = orderJpaRepository.save(orderModel);
     return mapper.toDomain(savedModel);
    }

    @Override
    public void softDelete(Order order) {
        OrderModel orderModel = mapper.fromDomain(order);
        orderModel.setDeletedAt(LocalDateTime.now());

        orderJpaRepository.save(orderModel);
    }

    @Override
    public void hardDelete(Order order) {
        OrderModel orderModel = mapper.fromDomain(order);
        orderJpaRepository.delete(orderModel);
    }

    @Override
    public Optional<Order> findByID(OrderID orderID) {
        return orderJpaRepository.findById(orderID.value())
                .map(mapper::toDomain);

    }

    @Override
    public Optional<Order> findByUserIDAndOrderID(UserID customerID, OrderID orderID) {
        Optional<OrderModel> orderModelOpt = orderJpaRepository.findByUserIdAndId(
                customerID.toString(),
                orderID.toString()
        );
        return orderModelOpt.map(mapper::toDomain);
    }
}
