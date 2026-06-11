package io.github.alexisTrejo11.drugstore.order.orders.infrastructure.persistence.repository;

import libs_kernel.mapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.order.orders.application.query.request.SearchOrdersQuery;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.Order;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.enums.OrderStatus;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.AddressID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.repository.OrderRepository;
import io.github.alexisTrejo11.drugstore.order.orders.infrastructure.persistence.models.DeliveryInfoModel;
import io.github.alexisTrejo11.drugstore.order.orders.infrastructure.persistence.models.OrderModel;
import io.github.alexisTrejo11.drugstore.order.orders.infrastructure.persistence.models.PickupInfoModel;
import io.github.alexisTrejo11.drugstore.order.orders.infrastructure.persistence.specification.OrderSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
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
    public Optional<Order> findByIDAndUserID(OrderID orderID, UserID customerID) {
        Optional<OrderModel> orderModelOpt = orderJpaRepository.findByUserIdAndId(
                customerID.toString(),
                orderID.toString());
        return orderModelOpt.map(mapper::toDomain);
    }

    @Override
    public boolean existsAnyByAddressIDAndOngoingStatus(AddressID addressID) {
        List<OrderStatus> ongoingStatuses = OrderStatus.getActiveStatuses();

        return orderJpaRepository.existsByAddressIdAndStatusInNative(
                addressID.value(),
                ongoingStatuses.stream().map(OrderStatus::name).toList());
    }

    @Override
    public Long countByUserID(UserID customerID) {
        return orderJpaRepository.countByUserId(customerID.toString());
    }

    @Override
    public Long countByUserIDAndStatus(UserID customerID, OrderStatus status) {
        return orderJpaRepository.countByUserIdAndStatus(
                customerID.toString(),
                status.toString());
    }

    @Override
    public Order save(Order order) {
        OrderModel orderModel = mapper.fromDomain(order);
        orderJpaRepository.findById(orderModel.getId()).ifPresent(existing -> mergePersistenceFields(existing, orderModel));

        OrderModel savedModel = orderJpaRepository.save(orderModel);
        return mapper.toDomain(savedModel);
    }

    @Override
    public void softDelete(Order order) {
        OrderModel orderModel = mapper.fromDomain(order);
        orderJpaRepository.findById(orderModel.getId()).ifPresent(existing -> mergePersistenceFields(existing, orderModel));
        orderModel.setDeletedAt(LocalDateTime.now());

        orderJpaRepository.save(orderModel);
    }

    /**
     * Rows are rebuilt from the domain model without audit timestamps; preserve DB values
     * so updates do not write NULL into NOT NULL columns.
     */
    private static void mergePersistenceFields(OrderModel existing, OrderModel incoming) {
        if (existing.getCreatedAt() != null) {
            incoming.setCreatedAt(existing.getCreatedAt());
        }
        if (existing.getUpdatedAt() != null) {
            incoming.setUpdatedAt(existing.getUpdatedAt());
        }
        if (existing.getUserID() != null) {
            incoming.setUserID(existing.getUserID());
        }
        DeliveryInfoModel inDel = incoming.getDeliveryInfo();
        DeliveryInfoModel exDel = existing.getDeliveryInfo();
        if (inDel != null && exDel != null) {
            if (exDel.getCreatedAt() != null) {
                inDel.setCreatedAt(exDel.getCreatedAt());
            }
            if (exDel.getUpdatedAt() != null) {
                inDel.setUpdatedAt(exDel.getUpdatedAt());
            }
        }
        PickupInfoModel inPu = incoming.getPickupInfo();
        PickupInfoModel exPu = existing.getPickupInfo();
        if (inPu != null && exPu != null) {
            if (exPu.getCreatedAt() != null) {
                inPu.setCreatedAt(exPu.getCreatedAt());
            }
            if (exPu.getUpdatedAt() != null) {
                inPu.setUpdatedAt(exPu.getUpdatedAt());
            }
        }
    }

    @Override
    public void hardDelete(Order order) {
        OrderModel orderModel = mapper.fromDomain(order);
        orderJpaRepository.delete(orderModel);
    }

    @Override
    public Page<Order> findBySpecification(SearchOrdersQuery query) {
        Specification<OrderModel> spec = OrderSpecifications.withSearchCriteria(query);
        Page<OrderModel> orderModels = orderJpaRepository.findAll(spec, query.pageable());
        return orderModels.map(mapper::toDomain);
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
                orderID.toString());
        return orderModelOpt.map(mapper::toDomain);
    }
}
