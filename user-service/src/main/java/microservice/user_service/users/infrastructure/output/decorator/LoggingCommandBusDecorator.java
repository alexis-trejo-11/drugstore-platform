package microservice.user_service.users.infrastructure.output.decorator;



import microservice.user_service.users.core.ports.input.CommandBus;
import microservice.user_service.users.core.application.command.Command;
import microservice.user_service.users.core.application.dto.CommandResult;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Order(1)
public class LoggingCommandBusDecorator implements CommandBus {

    private static final Logger logger = LoggerFactory.getLogger(LoggingCommandBusDecorator.class);
    private final CommandBus delegate;

    public LoggingCommandBusDecorator(CommandBus delegate) {
        this.delegate = delegate;
    }

    @Override
    public CommandResult dispatch(Command command) {
        logger.info("Executing command: {}", command.getClass().getSimpleName());
        long startTime = System.currentTimeMillis();

        try {
            var result = delegate.dispatch(command);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Command {} executed successfully in {}ms",
                    command.getClass().getSimpleName(), duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Command {} failed after {}ms: {}",
                    command.getClass().getSimpleName(), duration, e.getMessage());
            throw e;
        }
    }
}

