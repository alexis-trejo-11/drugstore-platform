package microservice.user_service.users.core.application.handlers;

import microservice.user_service.users.core.application.command.Command;
import microservice.user_service.users.core.application.dto.CommandResult;

public interface CommandHandler<T extends Command> {

    CommandResult handle(T command);
}
