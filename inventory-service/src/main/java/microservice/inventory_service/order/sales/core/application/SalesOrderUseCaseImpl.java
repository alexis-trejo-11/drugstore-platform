package microservice.inventory_service.order.sales.core.application;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.order.sales.core.application.command.*;
import microservice.inventory_service.order.sales.core.application.query.GetSaleOrdersByCustomerId;
import microservice.inventory_service.order.sales.core.application.query.GetSaleOrdersById;
import microservice.inventory_service.order.sales.core.application.query.GetSaleOrdersByStatus;
import microservice.inventory_service.order.sales.core.domain.entity.SaleOrder;
import microservice.inventory_service.order.sales.core.domain.port.SaleOrderRepository;
import microservice.inventory_service.order.sales.core.domain.port.SalesOrderUseCase;
import microservice.inventory_service.order.supplier_purchase.domain.exception.OrderNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalesOrderUseCaseImpl implements SalesOrderUseCase {
    private final SaleOrderRepository repository;

    @Override
    @Transactional(readOnly = true)
    public SaleOrder getSalesOrderById(GetSaleOrdersById query) {
        return repository.findById(query.orderId())
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + query.orderId() + " not found"));
    }

    @Override
    public Page<SaleOrder> getSalesOrdersByCustomerUserId(GetSaleOrdersByCustomerId query) {
        return repository.findByCustomerId(query.customerId(), query.pageable());
    }

    @Override
    public Page<SaleOrder> getSalesOrdersByStatus(GetSaleOrdersByStatus query) {
        return null;
    }

    @Override
    @Transactional
    public void confirmPayment(ConfirmSaleOrderCommand command) {
        var entity = repository.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + command.orderId() + " not found"));

        entity.confirmPayment(command.paymentId());
        repository.save(entity);
    }

    @Override
    @Transactional
    public void receiveSaleOrder(ReceiveOrderSaleCommand command) {
        command.validate();

        var params = command.toCreateSalesOrderParams();
        SaleOrder receivedSaleOrder = SaleOrder.createNew(params);

        repository.save(receivedSaleOrder);
    }


    @Override
    public void fulfillOrder(FullFillOrderCommand command) {
        var entity = repository.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + command.orderId() + " not found"));

        entity.fulfillOrder();
        repository.save(entity);
    }

    @Override
    public void readyToDelivery(SendSaleOrderToDeliveryCommand command) {
        var entity = repository.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + command.orderId() + " not found"));

        entity.readyToDelivery();
        repository.save(entity);
    }


    @Override
    public void cancelOrder(CancelSaleOrderCommand command) {
        var entity = repository.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + command.orderId() + " not found"));

        entity.cancelOrder(command.reason());
        repository.save(entity);
    }

    @Override
    public void delete(DeleteSaleOrderCommand command) {
        repository.deleteById(command.orderId());

    }
}
