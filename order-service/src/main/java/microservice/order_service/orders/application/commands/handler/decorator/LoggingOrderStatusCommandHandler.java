package microservice.order_service.orders.application.commands.handler.decorator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.order_service.orders.application.commands.handler.OrderStatusCommandHandler;
import microservice.order_service.orders.application.commands.handler.OrderStatusCommandHandlerImpl;
import microservice.order_service.orders.application.commands.request.status.*;
import microservice.order_service.orders.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.orders.application.commands.response.UpdateOrderStatusCommandResult;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class LoggingOrderStatusCommandHandler implements OrderStatusCommandHandler {
    private final OrderStatusCommandHandlerImpl delegate;

    @Override
    public UpdateOrderStatusCommandResult handle(PrepareOrderCommand command) {
        log.info("Prepare order: purchaseOrderId={}", command.orderID());
        long start = System.currentTimeMillis();
        try {
            UpdateOrderStatusCommandResult result = delegate.handle(command);
            long duration = System.currentTimeMillis() - start;
            log.info("Order prepared: purchaseOrderId={}, prevStatus={}, duration={}ms", result.orderId(), result.previousStatus(), duration);
            return result;
        } catch (Exception ex) {
            log.error("Failed to prepare order: purchaseOrderId={}, error={}", command.orderID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public UpdateOrderStatusCommandResult handle(OrderReadyToPickupCommand command) {
        log.info("Mark order ready for pickup: purchaseOrderId={}", command.orderID());
        long start = System.currentTimeMillis();
        try {
            UpdateOrderStatusCommandResult result = delegate.handle(command);
            long duration = System.currentTimeMillis() - start;
            log.info("Order ready for pickup: purchaseOrderId={}, prevStatus={}, duration={}ms", result.orderId(), result.previousStatus(), duration);
            return result;
        } catch (Exception ex) {
            log.error("Failed to mark order ready for pickup: purchaseOrderId={}, error={}", command.orderID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public UpdateOrderStatusCommandResult handle(ConfirmOrderCommand command) {
        log.info("Confirm order: purchaseOrderId={}, paymentId={}", command.orderID(), command.paymentID());
        long start = System.currentTimeMillis();
        try {
            UpdateOrderStatusCommandResult result = delegate.handle(command);
            long duration = System.currentTimeMillis() - start;
            log.info("Order confirmed: purchaseOrderId={}, prevStatus={}, duration={}ms", result.orderId(), result.previousStatus(), duration);
            return result;
        } catch (Exception ex) {
            log.error("Failed to confirm order: purchaseOrderId={}, error={}", command.orderID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public UpdateOrderStatusCommandResult handle(ShipOrderCommand command) {
        log.info("Ship order: purchaseOrderId={}, tracking={}", command.orderID(), command.deliveryTrackNumber());
        long start = System.currentTimeMillis();
        try {
            UpdateOrderStatusCommandResult result = delegate.handle(command);
            long duration = System.currentTimeMillis() - start;
            log.info("Order shipped: purchaseOrderId={}, prevStatus={}, duration={}ms", result.orderId(), result.previousStatus(), duration);
            return result;
        } catch (Exception ex) {
            log.error("Failed to ship order: purchaseOrderId={}, error={}", command.orderID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public UpdateOrderStatusCommandResult handle(CompleteOrderCommand command) {
        log.info("Complete order: purchaseOrderId={}", command.orderID());
        long start = System.currentTimeMillis();
        try {
            UpdateOrderStatusCommandResult result = delegate.handle(command);
            long duration = System.currentTimeMillis() - start;
            log.info("Order completed: purchaseOrderId={}, prevStatus={}, duration={}ms", result.orderId(), result.previousStatus(), duration);
            return result;
        } catch (Exception ex) {
            log.error("Failed to complete order: purchaseOrderId={}, error={}", command.orderID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public UpdateOrderStatusCommandResult handle(OrderDeliverFailCommand command) {
        log.info("Mark order delivery failed: purchaseOrderId={}, reason={}", command.orderID(), command.reason());
        long start = System.currentTimeMillis();
        try {
            UpdateOrderStatusCommandResult result = delegate.handle(command);
            long duration = System.currentTimeMillis() - start;
            log.info("Order delivery marked as failed: purchaseOrderId={}, prevStatus={}, duration={}ms", result.orderId(), result.previousStatus(), duration);
            return result;
        } catch (Exception ex) {
            log.error("Failed to mark delivery failed: purchaseOrderId={}, error={}", command.orderID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public CancelOrderCommandResponse handle(CancelOrderCommand command) {
        var user = command.userID();
        log.info("Cancel order: purchaseOrderId={}, userId={}, reason={}", command.orderID(), (user == null ? "<admin>" : user), command.reason());
        long start = System.currentTimeMillis();
        try {
            CancelOrderCommandResponse response = delegate.handle(command);
            long duration = System.currentTimeMillis() - start;
            log.info("Order cancelled: purchaseOrderId={}, duration={}ms", response.orderId(), duration);
            return response;
        } catch (Exception ex) {
            log.error("Failed to cancel order: purchaseOrderId={}, error={}", command.orderID(), ex.getMessage(), ex);
            throw ex;
        }
    }
}
