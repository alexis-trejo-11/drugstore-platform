package user_service.modules.users.core.ports.input;

import user_service.modules.users.core.application.command.Command;
import user_service.modules.users.core.application.dto.CommandResult;

public interface CommandHandler<T extends Command> {

    CommandResult handle(T command);
}
