package microservice.inventory_service.purchase.infrastructure.adapter.outbound.persistence.repository;

import libs_kernel.mapper.EntityMapper;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrderId;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrderStatus;
import microservice.inventory_service.purchase.domain.port.output.PurchaseOrderRepository;
import microservice.inventory_service.purchase.infrastructure.adapter.outbound.persistence.model.PurchaseOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class PurchaseOrderRepositoryImpl implements PurchaseOrderRepository {
    private EntityMapper<PurchaseOrderEntity, PurchaseOrder> mapper;
    private JpaPurchaseOrderRepository jpaRepository;

    @Override
    public PurchaseOrder save(PurchaseOrder purchaseOrder) {
        PurchaseOrderEntity orderEntity =  mapper.fromDomain(purchaseOrder);
        PurchaseOrderEntity savedEntity = jpaRepository.save(orderEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<PurchaseOrder> findById(PurchaseOrderId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<PurchaseOrder> findByOrderNumber(String orderNumber) {
        return jpaRepository.findByOrderNumber(orderNumber).map(mapper::toDomain);
    }

    @Override
    public Page<PurchaseOrder> findBySupplierId(String supplierId, Pageable pageable) {
        return jpaRepository.findBySupplierId(supplierId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<PurchaseOrder> findByStatus(PurchaseOrderStatus status, Pageable pageable) {
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
}
