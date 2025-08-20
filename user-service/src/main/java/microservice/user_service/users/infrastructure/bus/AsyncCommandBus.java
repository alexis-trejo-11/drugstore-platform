package microservice.user_service.users.infrastructure.bus;

import microservice.user_service.users.core.domain.ports.input.CommandBus;
import microservice.user_service.users.core.application.command.Command;
import microservice.user_service.users.core.application.dto.CommandResult;
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
