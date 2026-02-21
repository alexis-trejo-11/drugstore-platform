package io.github.alexisTrejo11.drugstore.users.user.adapter.input.bus.decorator;

import java.util.Set;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import io.github.alexisTrejo11.drugstore.users.user.core.application.command.Command;
import io.github.alexisTrejo11.drugstore.users.user.core.application.result.CommandResult;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.input.CommandBus;

@Component
@Order(2)
public class ValidationCommandBusDecorator implements CommandBus {

    private final CommandBus delegate;
    private final Validator validator;

    public ValidationCommandBusDecorator(CommandBus delegate, Validator validator) {
        this.delegate = delegate;
        this.validator = validator;
    }

    @Override
    public CommandResult dispatch(Command command) {
        Set<ConstraintViolation<Command>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            throw new ValidationException("Command validation failed " + violations);
        }

        return delegate.dispatch(command);
    }
}
