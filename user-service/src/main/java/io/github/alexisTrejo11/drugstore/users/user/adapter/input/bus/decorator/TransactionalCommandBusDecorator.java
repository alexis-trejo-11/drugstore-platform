package io.github.alexisTrejo11.drugstore.users.user.adapter.input.bus.decorator;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import io.github.alexisTrejo11.drugstore.users.user.core.application.command.Command;
import io.github.alexisTrejo11.drugstore.users.user.core.application.result.CommandResult;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.input.CommandBus;

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
