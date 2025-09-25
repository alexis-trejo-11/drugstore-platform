package microservice.order_service.application.service;

import lombok.RequiredArgsConstructor;
import microservice.order_service.application.commands.request.CancelOrderCommand;
import microservice.order_service.application.commands.request.CreateOrderCommand;
import microservice.order_service.application.commands.request.UpdateOrderStatusCommand;
import microservice.order_service.application.commands.response.CancelOrderCommandResponse;
import microservice.order_service.application.commands.response.CreateOrderCommandResponse;
import microservice.order_service.application.commands.response.UpdateOrderStatusCommandResponse;
import microservice.order_service.application.commands.hander.OrderCommandHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCommandService {

    private final OrderCommandHandler commandHandler;

    public CreateOrderCommandResponse createOrder(CreateOrderCommand command) {
        return commandHandler.handle(command);
    }

    public UpdateOrderStatusCommandResponse updateOrderStatus(UpdateOrderStatusCommand command) {
        return commandHandler.handle(command);
    }

    public CancelOrderCommandResponse cancelOrder(CancelOrderCommand command) {
        return commandHandler.handle(command);
    }
}