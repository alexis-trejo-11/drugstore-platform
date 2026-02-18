package user_service.modules.users.adapter.input.bus;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import user_service.modules.users.core.application.command.Command;
import user_service.modules.users.core.application.result.CommandResult;
import user_service.modules.users.core.ports.input.CommandBus;

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
