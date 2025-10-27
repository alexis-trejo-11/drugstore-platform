package microservice.inventory_service.external.sales_order.service;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.purachse_order.domain.exception.OrderNotFoundException;
import microservice.inventory_service.external.sales_order.repository.JpaSaleOrderRepository;
import microservice.inventory_service.external.sales_order.service.dto.SalesOrderDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalesOrderUseCaseImpl implements SalesOrderUseCase {
    private final JpaSaleOrderRepository repository;

    @Override
    @Transactional(readOnly = true)
    public SalesOrderDTO getSalesOrderById(String id) {
        var entity =  repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + id + " not found"));

        return SalesOrderDTO.fromEntity(entity);
    }

    @Override
    @Transactional
    public void confirmPayment(String orderId, String paymentId) {
        var entity = repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + orderId + " not found"));

        entity.confirmPayment(paymentId);
        repository.save(entity);
    }

    @Override
    @Transactional
    public String createSalesOrder(SalesOrderDTO command) {
        command.validate();
        var entity = repository.save(command.toEntity());

        var entitySaved = repository.save(entity);
        return entitySaved.getId();
    }

    @Override
    public void fulfillOrder(String id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + id + " not found"));

        entity.fulfillOrder();
        repository.save(entity);
    }

    @Override
    public void readyToDelivery(String id) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + id + " not found"));

        entity.readyToDelivery();
        repository.save(entity);
    }

    @Override
    public void cancelOrder(String id, String reason) {
        var entity = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + id + " not found"));

        entity.cancelOrder(reason);
        repository.save(entity);
    }
}
