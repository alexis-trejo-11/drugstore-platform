package microservice.inventory_service.external.order.infrastructure.adapter.outbound.persistence.repository;

import libs_kernel.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.domain.entity.Order;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderId;
import microservice.inventory_service.external.order.domain.entity.valueobject.OrderStatus;
import microservice.inventory_service.external.order.domain.port.output.OrderRepository;
import microservice.inventory_service.external.order.infrastructure.adapter.outbound.persistence.model.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final EntityMapper<OrderEntity, Order> mapper;
    private final JpaOrderRepository jpaRepository;

    @Override
    public Order save(Order Order) {
        OrderEntity orderEntity =  mapper.fromDomain(Order);
        OrderEntity savedEntity = jpaRepository.save(orderEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return jpaRepository.findByOrderNumber(orderNumber).map(mapper::toDomain);
    }

    @Override
    public Page<Order> findBySupplierId(String supplierId, Pageable pageable) {
        return jpaRepository.findBySupplierId(supplierId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Order> findByStatus(OrderStatus status, Pageable pageable) {
        return jpaRepository.findByStatus(status, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Order> findByExpectedDeliveryDateBefore(LocalDateTime date, Pageable pageable) {
        return jpaRepository.findByExpectedDeliveryDateBefore(date, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public void delete(OrderId id) {
        jpaRepository.deleteById(id.value());

    }

    @Override
    public boolean existsById(OrderId id) {
        return jpaRepository.existsById(id.value());
    }
}
