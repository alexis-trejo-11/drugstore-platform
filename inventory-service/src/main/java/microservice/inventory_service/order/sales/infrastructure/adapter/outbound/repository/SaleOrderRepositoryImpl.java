package microservice.inventory_service.order.sales.infrastructure.adapter.outbound.repository;

import libs_kernel.mapper.JpaEntityMapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.order.sales.core.domain.entity.SaleOrder;
import microservice.inventory_service.order.sales.core.domain.entity.valueobject.SaleOrderId;
import microservice.inventory_service.order.sales.core.domain.port.SaleOrderRepository;
import microservice.inventory_service.order.sales.infrastructure.adapter.outbound.models.SaleOrderEntity;
import microservice.inventory_service.shared.domain.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class SaleOrderRepositoryImpl implements SaleOrderRepository {
    private final JpaSaleOrderRepository jpaRepository;
    private final JpaEntityMapper<SaleOrderEntity, SaleOrder> saleOrderMapper;

    @Override
    public Optional<SaleOrder> findById(SaleOrderId saleOrderId) {
        return jpaRepository.findById(saleOrderId.value())
                .map(saleOrderMapper::toDomain);
    }

    @Override
    public Page<SaleOrder> findByCustomerId(String userId, Pageable pageable) {
        return jpaRepository.findByCustomerUserId(userId, pageable)
                .map(saleOrderMapper::toDomain);
    }

    @Override
    public Page<SaleOrder> findByStatus(OrderStatus status, Pageable pageable) {
        return jpaRepository.findByStatus(status.name(), pageable)
                .map(saleOrderMapper::toDomain);
    }

    @Override
    public SaleOrder save(SaleOrder saleOrder) {
        SaleOrderEntity entity = saleOrderMapper.fromDomain(saleOrder);
        SaleOrderEntity savedEntity = jpaRepository.save(entity);
        return saleOrderMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsById(SaleOrderId saleOrderId) {
        return jpaRepository.existsById(saleOrderId.value());
    }

    @Override
    public void deleteById(SaleOrderId saleOrderId) {
        jpaRepository.deleteById(saleOrderId.value());
    }
}
