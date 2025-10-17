package microservice.order_service.orders.application.commands.handler.decorator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.order_service.orders.application.commands.handler.OrderCommandHandler;
import microservice.order_service.orders.application.commands.handler.OrderCommandHandlerImpl;
import microservice.order_service.orders.application.commands.request.*;
import microservice.order_service.orders.application.commands.response.CreateOrderCommandResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class LoggingOrderCommandHandlerDecorator implements OrderCommandHandler {
    private final OrderCommandHandlerImpl delegate;

    @Override
    public CreateOrderCommandResponse handle(CreateDeliveryOrderCommand command) {
        log.info("Creating order: userId={}, deliveryMethod={}, itemCount={}",
                command.getUserID(), command.getDeliveryMethod(), command.getItems().size());

        long startTime = System.currentTimeMillis();

        try {
            CreateOrderCommandResponse response = delegate.handle(command);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Order created successfully: orderId={}, duration={}ms",
                    response.getOrderId(), duration);

            return response;

        } catch (Exception ex) {
            log.error("Order creation failed: userId={}, error={}",
                    command.getUserID(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public CreateOrderCommandResponse handle(CreatePickupOrderCommand command) {
        log.info("Creating pickup order: userId={}, deliveryMethod={}, itemCount={}",
                command.getUserID(), command.getDeliveryMethod(), command.getItems().size());

        long startTime = System.currentTimeMillis();

        try {
            CreateOrderCommandResponse response = delegate.handle(command);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Pickup order created successfully: orderId={}, duration={}ms",
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
        log.info("Updating order address: orderId={}, newAddressId={}",
                command.orderID(), command.addressID());

        try {
            delegate.handle(command);
            log.info("Order address updated: orderId={}", command.orderID());

        } catch (Exception ex) {
            log.warn("Update address failed: orderId={}, error={}",
                    command.orderID(), ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void handle(UpdateOrderDeliverMethodCommand command) {
        log.info("Updating delivery method: orderId={}, newMethod={}",
                command.orderID(), command.newMethod());

        try {
            delegate.handle(command);
            log.info("Delivery method updated: orderId={}", command.orderID());

        } catch (Exception ex) {
            log.warn("Update delivery method failed: orderId={}, error={}",
                    command.orderID(), ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void handle(DeleteOrderCommand command) {
        log.info("Deleting order: orderId={}, hardDelete={}",
                command.orderID(), command.isHardDelete());

        try {
            delegate.handle(command);
            log.info("Order deleted: orderId={}", command.orderID());

        } catch (Exception ex) {
            log.warn("Delete order failed: orderId={}, error={}",
                    command.orderID(), ex.getMessage());
            throw ex;
        }
    }
}
