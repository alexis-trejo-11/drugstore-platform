package io.github.alexisTrejo11.drugstore.users.app.user.adapter.input.bus;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.command.Command;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.result.CommandResult;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input.CommandBus;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input.CommandHandler;

public class SpringCommandBus implements CommandBus {
    private final Map<Class<? extends Command>, CommandHandler<? extends Command>> handlers = new HashMap<>();
    private final List<CommandHandler<? extends Command>> commandHandlers;

    @Autowired
    public SpringCommandBus(List<CommandHandler<? extends Command>> commandHandlers) {
        this.commandHandlers = commandHandlers;
    }

    @PostConstruct
    public void initializeHandlers() {
        commandHandlers.forEach(handler -> {
            Class<? extends Command> commandType = getCommandType(handler);
            if (handlers.containsKey(commandType)) {
                throw new IllegalStateException(
                        "Multiple handlers found for command: " + commandType.getSimpleName());
            }
            handlers.put(commandType, handler);
            System.out.println("Registered command handler: " + handler.getClass().getSimpleName() +
                    " for command: " + commandType.getSimpleName());
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult dispatch(Command command) {
        CommandHandler<Command> handler = (CommandHandler<Command>) handlers.get(command.getClass());
        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler registered for command: " + command.getClass().getSimpleName());
        }

        return handler.handle(command);

    }

    @SuppressWarnings("unchecked")
    private Class<? extends Command> getCommandType(CommandHandler<? extends Command> handler) {
        Type[] genericInterfaces = handler.getClass().getGenericInterfaces();

        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                if (parameterizedType.getRawType().equals(CommandHandler.class)) {
                    return (Class<? extends Command>) parameterizedType.getActualTypeArguments()[0];
                }
            }
        }

        throw new IllegalArgumentException(
                "Could not determine command type for handler: " + handler.getClass().getSimpleName());
    }

    public Map<Class<? extends Command>, CommandHandler<? extends Command>> getRegisteredHandlers() {
        return new HashMap<>(handlers);
    }
}
