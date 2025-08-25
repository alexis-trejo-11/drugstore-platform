package microservice.user_service.users.infrastructure.adapter.bus.decorator;

import jakarta.transaction.Transactional;
import microservice.user_service.users.core.ports.input.CommandBus;
import microservice.user_service.users.core.application.command.Command;
import microservice.user_service.users.core.application.dto.CommandResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class TransactionalCommandBusDecorator implements CommandBus {
    private final CommandBus delegate;

    public TransactionalCommandBusDecorator(CommandBus delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public CommandResult dispatch(Command command) {
        return delegate.dispatch(command);
    }
}
