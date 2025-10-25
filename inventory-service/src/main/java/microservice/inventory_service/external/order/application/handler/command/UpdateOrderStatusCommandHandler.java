package microservice.inventory_service.external.order.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.command.UpdateOrderStatusCommand;
import microservice.inventory_service.external.order.domain.entity.Order;
import microservice.inventory_service.external.order.domain.exception.OrderNotFoundException;
import microservice.inventory_service.external.order.domain.port.output.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateOrderStatusCommandHandler {
    private final OrderRepository orderRepository;

    @Transactional
    public void handle(UpdateOrderStatusCommand command) {
        Order Order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        var newStatus = command.newStatus();
        switch (newStatus) {
            case APPROVED:
                Order.approve(command.updatedBy());
                break;
            case SENT:
                Order.sendToSupplier();
                break;
            case CANCELLED:
                Order.cancel();
                break;
            case REJECTED:
                Order.reject();
                break;
            default:
                throw new IllegalArgumentException("Invalid status update");
        }

        orderRepository.save(Order);
    }
}