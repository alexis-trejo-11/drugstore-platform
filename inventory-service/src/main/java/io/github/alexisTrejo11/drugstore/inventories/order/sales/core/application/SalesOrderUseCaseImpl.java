package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application;

import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.command.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.service.InventoryStockService;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.command.*;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.query.GetSaleOrdersByCustomerId;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.query.GetSaleOrdersById;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.application.query.GetSaleOrdersByStatus;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.SaleOrder;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.event.FulfillSaleOrderEvent;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.event.ReceiveSaleOrderEvent;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.event.UpdateSaleOrderStatusEvent;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.port.SaleOrderRepository;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.port.SalesOrderUseCase;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.exception.OrderNotFoundException;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderReference;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesOrderUseCaseImpl implements SalesOrderUseCase {
  private final SaleOrderRepository repository;
  private final ApplicationEventPublisher eventPublisher;
  private final InventoryStockService stockService;

  @Override
  @Transactional(readOnly = true)
  public SaleOrder getSalesOrderById(GetSaleOrdersById query) {
    return repository.findById(query.orderId())
        .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + query.orderId() + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<SaleOrder> getSalesOrdersByCustomerUserId(GetSaleOrdersByCustomerId query) {
    return repository.findByCustomerId(query.customerId(), query.pageable());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<SaleOrder> getSalesOrdersByStatus(GetSaleOrdersByStatus query) {
    return repository.findByStatus(query.status(), query.pageable());
  }

  @Override
  @Transactional
  public void confirmPayment(ConfirmSaleOrderCommand command) {
    log.info("Confirming payment for SaleOrder id: {}, paymentId: {}", command.orderId(), command.paymentId());
    var entity = repository.findById(command.orderId())
        .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + command.orderId() + " not found"));

    log.info("Confirming payment for SaleOrderId:{}", entity.getId());
    entity.confirmPayment(command.paymentId());

    log.info("Saving SaleOrderId:{} after payment confirmation", entity.getId());
    repository.save(entity);

    var event = new UpdateSaleOrderStatusEvent(entity, OrderStatus.APPROVED);
    eventPublisher.publishEvent(event);
    log.info("Published UpdateSaleOrderStatusEvent for SaleOrderId:{}", entity.getId());

    log.info("Payment confirmed and SaleOrderId:{} saved successfully", entity.getId());
  }

  @Override
  @Transactional
  public void receiveSaleOrder(ReceiveOrderSaleCommand command) {
    log.info("Receiving new SaleOrder for CustomerId: {}", command.customerId());

    var isStockAvailable = stockService.isStockAvailable(command.productQuantities());
    if (!isStockAvailable) {
      log.error("Insufficient stock for SaleOrder of CustomerId: {}", command.customerId());
      throw new IllegalStateException("Insufficient stock for one or more products in the order");
    }

    var params = command.toCreateSalesOrderParams();
    SaleOrder saleOrder = SaleOrder.create(params);
    log.info("Saving new SaleOrder for CustomerId: {}", command.customerId());

    repository.save(saleOrder);

    log.info("Publishing ReceiveSaleOrderEvent for SaleOrderId: {}", saleOrder.getId());
    var event = new ReceiveSaleOrderEvent(
        command.productQuantities(),
        OrderReference.forSaleOrder(saleOrder.getId()));
    eventPublisher.publishEvent(event);

    log.info("New SaleOrder for CustomerId: {} saved successfully with OrderId: {}", command.customerId(),
        saleOrder.getId());
  }

  @Override
  public void fulfillOrder(FullFillOrderCommand command) {
    log.info("Fulfilling SaleOrder id: {}", command.orderId());
    var entity = repository.findById(command.orderId())
        .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + command.orderId() + " not found"));

    log.info("Fulfilling SaleOrderId:{}", entity.getId());
    entity.fulfillOrder();

    log.info("Saving SaleOrderId:{} after fulfilling the order", entity.getId());
    repository.save(entity);

    var event = new FulfillSaleOrderEvent(entity);
    eventPublisher.publishEvent(event);
    log.info("Published FulfillSaleOrderEvent for SaleOrderId:{}", entity.getId());

    log.info("SaleOrderId:{} fulfilled and saved successfully", entity.getId());
  }

  @Override
  public void readyToDelivery(SendSaleOrderToDeliveryCommand command) {
    var entity = repository.findById(command.orderId())
        .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + command.orderId() + " not found"));

    log.info("Marking SaleOrderId:{} as ready to delivery", entity.getId());
    entity.readyToDelivery();

    log.info("Saving SaleOrderId:{} after marking as ready to delivery", entity.getId());
    repository.save(entity);

    var event = new UpdateSaleOrderStatusEvent(entity, OrderStatus.READY_FOR_LEAVE);
    eventPublisher.publishEvent(event);
    log.info("Published UpdateSaleOrderStatusEvent for SaleOrderId:{}", entity.getId());

    log.info("SaleOrderId:{} marked as ready to delivery and saved successfully", entity.getId());
  }

  @Override
  public void cancelOrder(CancelSaleOrderCommand command) {
    log.info("Cancelling SaleOrder id: {}", command.orderId());
    var entity = repository.findById(command.orderId())
        .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder with id " + command.orderId() + " not found"));

    log.info("Cancelling SaleOrderId:{}", entity.getId());
    entity.cancelOrder(command.reason());

    log.info("Saving SaleOrderId:{} after cancelling the order", entity.getId());
    repository.save(entity);

    var event = new UpdateSaleOrderStatusEvent(entity, OrderStatus.CANCELLED);
    eventPublisher.publishEvent(event);
    log.info("Published UpdateSaleOrderStatusEvent for SaleOrderId:{}", entity.getId());

    log.info("SaleOrderId:{} cancelled and saved successfully", entity.getId());
  }

  @Override
  public void delete(DeleteSaleOrderCommand command) {
    log.info("Deleting SaleOrder id: {}", command.orderId());
    if (!repository.existsById(command.orderId())) {
      throw new OrderNotFoundException("SaleOrder with id " + command.orderId() + " not found");
    }
    log.info("Deleting SaleOrder id: {}", command.orderId());

    repository.deleteById(command.orderId());
    log.info("SaleOrder id: {} deleted successfully", command.orderId());
  }
}
