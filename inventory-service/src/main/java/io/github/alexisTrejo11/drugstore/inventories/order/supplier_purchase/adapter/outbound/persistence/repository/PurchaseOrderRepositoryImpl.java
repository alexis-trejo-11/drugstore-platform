package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.adapter.outbound.persistence.repository;

import libs_kernel.mapper.JpaEntityMapper;
import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.adapter.outbound.persistence.model.PurchaseOrderEntityOrder;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.PurchaseOrder;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.PurchaseOrderId;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.OrderStatus;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.port.output.PurchaseOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PurchaseOrderRepositoryImpl implements PurchaseOrderRepository {
    private final JpaEntityMapper<PurchaseOrderEntityOrder, PurchaseOrder> mapper;
    private final JpaOrderRepository jpaRepository;

    @Override
    public PurchaseOrder save(PurchaseOrder PurchaseOrder) {
        PurchaseOrderEntityOrder purchaseOrderEntity =  mapper.fromDomain(PurchaseOrder);
        PurchaseOrderEntityOrder savedEntity = jpaRepository.save(purchaseOrderEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<PurchaseOrder> findById(PurchaseOrderId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Page<PurchaseOrder> findBySupplierId(String supplierId, Pageable pageable) {
        return jpaRepository.findBySupplierId(supplierId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<PurchaseOrder> findByStatus(OrderStatus status, Pageable pageable) {
        return jpaRepository.findByStatus(status, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<PurchaseOrder> findByExpectedDeliveryDateBefore(LocalDateTime date, Pageable pageable) {
        return jpaRepository.findByExpectedDeliveryDateBefore(date, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public void delete(PurchaseOrderId id) {
        jpaRepository.deleteById(id.value());

    }

    @Override
    public boolean existsById(PurchaseOrderId id) {
        return jpaRepository.existsById(id.value());
    }
}
