package io.github.alexisTrejo11.drugstore.order.orders.application.handler.command.decorator;

import io.github.alexisTrejo11.drugstore.order.orders.application.commands.request.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.order.orders.application.handler.command.OrderCommandHandler;
import io.github.alexisTrejo11.drugstore.order.orders.application.handler.command.OrderCommandHandlerImpl;
import io.github.alexisTrejo11.drugstore.order.orders.application.commands.response.CreateOrderOperationSummary;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class LoggingOrderCommandHandlerDecorator implements OrderCommandHandler {
    private final OrderCommandHandlerImpl delegate;

    @Override
    public CreateOrderOperationSummary handle(CreateDeliveryOrderCommand command) {
        log.info("Creating order: userId={}, deliveryMethod={}, itemCount={}",
                command.getUserID(), command.getDeliveryMethod(), command.getItems().size());

        long startTime = System.currentTimeMillis();

        try {
            CreateOrderOperationSummary response = delegate.handle(command);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Order created successfully: purchaseOrderId={}, duration={}ms",
                    response.getOrderId(), duration);

            return response;

        } catch (Exception ex) {
            log.error("Order creation failed: userId={}, error={}",
                    command.getUserID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public CreateOrderOperationSummary handle(CreatePickupOrderCommand command) {
        log.info("Creating pickup order: userId={}, deliveryMethod={}, itemCount={}",
                command.getUserID(), command.getDeliveryMethod(), command.getItems().size());

        long startTime = System.currentTimeMillis();

        try {
            CreateOrderOperationSummary response = delegate.handle(command);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Pickup order created successfully: purchaseOrderId={}, duration={}ms",
                    response.getOrderId(), duration);

            return response;

        } catch (Exception ex) {
            log.error("Pickup Order creation failed: userId={}, error={}",
                    command.getUserID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void handle(UpdateOrderAddressCommand command) {
        log.info("Updating order address: purchaseOrderId={}, newAddressId={}",
                command.orderID(), command.addressID());

        try {
            delegate.handle(command);
            log.info("Order address updated: purchaseOrderId={}", command.orderID());

        } catch (Exception ex) {
            log.warn("Update address failed: purchaseOrderId={}, error={}",
                    command.orderID(), ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void handle(UpdateOrderDeliverMethodCommand command) {
        log.info("Updating delivery method: purchaseOrderId={}, newMethod={}",
                command.orderID(), command.newMethod());

        try {
            delegate.handle(command);
            log.info("Delivery method updated: purchaseOrderId={}", command.orderID());

        } catch (Exception ex) {
            log.warn("Update delivery method failed: purchaseOrderId={}, error={}",
                    command.orderID(), ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void handle(DeleteOrderCommand command) {
        log.info("Deleting order: purchaseOrderId={}, hardDelete={}",
                command.orderID(), command.isHardDelete());

        try {
            delegate.handle(command);
            log.info("Order deleted: purchaseOrderId={}", command.orderID());

        } catch (Exception ex) {
            log.warn("Delete order failed: purchaseOrderId={}, error={}",
                    command.orderID(), ex.getMessage());
            throw ex;
        }
    }
}
