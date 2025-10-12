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
        log.info("Prepare order: orderId={}", command.orderID());
        long start = System.currentTimeMillis();
        try {
            UpdateOrderStatusCommandResult result = delegate.handle(command);
            long duration = System.currentTimeMillis() - start;
            log.info("Order prepared: orderId={}, prevStatus={}, duration={}ms", result.orderId(), result.previousStatus(), duration);
            return result;
        } catch (Exception ex) {
            log.error("Failed to prepare order: orderId={}, error={}", command.orderID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public UpdateOrderStatusCommandResult handle(OrderReadyToPickupCommand command) {
        log.info("Mark order ready for pickup: orderId={}", command.orderID());
        long start = System.currentTimeMillis();
        try {
            UpdateOrderStatusCommandResult result = delegate.handle(command);
            long duration = System.currentTimeMillis() - start;
            log.info("Order ready for pickup: orderId={}, prevStatus={}, duration={}ms", result.orderId(), result.previousStatus(), duration);
            return result;
        } catch (Exception ex) {
            log.error("Failed to mark order ready for pickup: orderId={}, error={}", command.orderID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public UpdateOrderStatusCommandResult handle(ConfirmOrderCommand command) {
        log.info("Confirm order: orderId={}, paymentId={}", command.orderID(), command.paymentID());
        long start = System.currentTimeMillis();
        try {
            UpdateOrderStatusCommandResult result = delegate.handle(command);
            long duration = System.currentTimeMillis() - start;
            log.info("Order confirmed: orderId={}, prevStatus={}, duration={}ms", result.orderId(), result.previousStatus(), duration);
            return result;
        } catch (Exception ex) {
            log.error("Failed to confirm order: orderId={}, error={}", command.orderID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public UpdateOrderStatusCommandResult handle(ShipOrderCommand command) {
        log.info("Ship order: orderId={}, tracking={}", command.orderID(), command.deliveryTrackNumber());
        long start = System.currentTimeMillis();
        try {
            UpdateOrderStatusCommandResult result = delegate.handle(command);
            long duration = System.currentTimeMillis() - start;
            log.info("Order shipped: orderId={}, prevStatus={}, duration={}ms", result.orderId(), result.previousStatus(), duration);
            return result;
        } catch (Exception ex) {
            log.error("Failed to ship order: orderId={}, error={}", command.orderID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public UpdateOrderStatusCommandResult handle(CompleteOrderCommand command) {
        log.info("Complete order: orderId={}", command.orderID());
        long start = System.currentTimeMillis();
        try {
            UpdateOrderStatusCommandResult result = delegate.handle(command);
            long duration = System.currentTimeMillis() - start;
            log.info("Order completed: orderId={}, prevStatus={}, duration={}ms", result.orderId(), result.previousStatus(), duration);
            return result;
        } catch (Exception ex) {
            log.error("Failed to complete order: orderId={}, error={}", command.orderID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public UpdateOrderStatusCommandResult handle(OrderDeliverFailCommand command) {
        log.info("Mark order delivery failed: orderId={}, reason={}", command.orderID(), command.reason());
        long start = System.currentTimeMillis();
        try {
            UpdateOrderStatusCommandResult result = delegate.handle(command);
            long duration = System.currentTimeMillis() - start;
            log.info("Order delivery marked as failed: orderId={}, prevStatus={}, duration={}ms", result.orderId(), result.previousStatus(), duration);
            return result;
        } catch (Exception ex) {
            log.error("Failed to mark delivery failed: orderId={}, error={}", command.orderID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public CancelOrderCommandResponse handle(CancelOrderCommand command) {
        var user = command.userID();
        log.info("Cancel order: orderId={}, userId={}, reason={}", command.orderID(), (user == null ? "<admin>" : user), command.reason());
        long start = System.currentTimeMillis();
        try {
            CancelOrderCommandResponse response = delegate.handle(command);
            long duration = System.currentTimeMillis() - start;
            log.info("Order cancelled: orderId={}, duration={}ms", response.orderId(), duration);
            return response;
        } catch (Exception ex) {
            log.error("Failed to cancel order: orderId={}, error={}", command.orderID(), ex.getMessage(), ex);
            throw ex;
        }
    }
}
