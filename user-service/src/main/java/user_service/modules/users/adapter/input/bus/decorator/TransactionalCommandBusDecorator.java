package user_service.modules.users.adapter.input.bus.decorator;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import user_service.modules.users.core.application.command.Command;
import user_service.modules.users.core.application.result.CommandResult;
import user_service.modules.users.core.ports.input.CommandBus;

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
