package microservice.users.core.application.handlers;

import microservice.users.core.application.command.Command;
import microservice.users.core.application.dto.CommandResult;

public interface CommandHandler<T extends Command> {

    CommandResult handle(T command);
}
