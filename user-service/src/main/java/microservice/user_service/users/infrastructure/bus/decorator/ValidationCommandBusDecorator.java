package microservice.user_service.users.infrastructure.bus.decorator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import microservice.user_service.users.core.domain.ports.input.CommandBus;
import microservice.user_service.users.core.application.command.Command;
import microservice.user_service.users.core.application.dto.CommandResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

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


