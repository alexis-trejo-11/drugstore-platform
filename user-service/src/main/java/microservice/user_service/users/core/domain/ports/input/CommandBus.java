package microservice.user_service.users.core.domain.ports.input;

import microservice.user_service.users.core.application.command.Command;
import microservice.user_service.users.core.application.dto.CommandResult;

public interface CommandBus {
    CommandResult dispatch(Command command);
}
