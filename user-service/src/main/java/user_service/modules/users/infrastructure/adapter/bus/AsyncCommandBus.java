package user_service.modules.users.infrastructure.adapter.bus;

import user_service.modules.users.core.ports.input.CommandBus;
import user_service.modules.users.core.application.command.Command;
import user_service.modules.users.core.application.dto.CommandResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component("asyncCommandBus")
public class AsyncCommandBus {

    private final CommandBus commandBus;

    public AsyncCommandBus(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Async
    public CompletableFuture<CommandResult> executeAsync(Command command) {
        return CompletableFuture.supplyAsync(() -> commandBus.dispatch(command));
    }

    public void dispatchFireAndForget(Command command) {
        CompletableFuture.runAsync(() -> commandBus.dispatch(command));
    }
}
