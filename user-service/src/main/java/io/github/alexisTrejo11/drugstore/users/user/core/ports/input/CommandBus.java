package io.github.alexisTrejo11.drugstore.users.user.core.ports.input;

import io.github.alexisTrejo11.drugstore.users.user.core.application.command.Command;
import io.github.alexisTrejo11.drugstore.users.user.core.application.result.CommandResult;

public interface CommandBus {
    CommandResult dispatch(Command command);
}
