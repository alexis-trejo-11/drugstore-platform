package io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input;

import io.github.alexisTrejo11.drugstore.users.app.user.core.application.command.Command;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.result.CommandResult;

public interface CommandHandler<T extends Command> {

    CommandResult handle(T command);
}
