package microservice.inventory_service.purchase.infrastructure.adapter.outbound.persistence.repository;

import microservice.inventory_service.purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrderId;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrderStatus;
import microservice.inventory_service.purchase.domain.port.output.PurchaseOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class PurchaseOrderRepositoryImpl implements PurchaseOrderRepository {
    @Override
    public PurchaseOrder save(PurchaseOrder purchaseOrder) {
        return null;
    }

    @Override
    public Optional<PurchaseOrder> findById(PurchaseOrderId id) {
        return Optional.empty();
    }

    @Override
    public Optional<PurchaseOrder> findByOrderNumber(String orderNumber) {
        return Optional.empty();
    }

    @Override
    public Page<PurchaseOrder> findBySupplierId(String supplierId) {
        return null;
    }

    @Override
    public Page<PurchaseOrder> findByStatus(PurchaseOrderStatus status) {
        return null;
    }

    @Override
    public Page<PurchaseOrder> findByExpectedDeliveryDateBefore(LocalDateTime date) {
        return null;
    }

    @Override
    public Page<PurchaseOrder> findAll() {
        return null;
    }

    @Override
    public void delete(String id) {

    }
}
