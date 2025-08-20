package microservice.user_service.config;

import microservice.user_service.users.core.domain.ports.input.CommandBus;
import microservice.user_service.users.core.domain.ports.input.QueryBus;
import microservice.user_service.users.core.application.command.Command;
import microservice.user_service.users.core.application.handlers.CommandHandler;
import microservice.user_service.users.core.application.handlers.QueryHandler;
import microservice.user_service.users.core.application.queries.Query;
import microservice.user_service.users.infrastructure.bus.SpringCommandBus;
import microservice.user_service.users.infrastructure.bus.SpringQueryBus;
import microservice.user_service.users.infrastructure.bus.decorator.LoggingCommandBusDecorator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class CqrsConfiguration {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "cqrs.bus.type", havingValue = "spring", matchIfMissing = true)
    public CommandBus springCommandBus(List<CommandHandler<? extends Command>> handlers) {
        return new SpringCommandBus(handlers);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "cqrs.query.type", havingValue = "spring", matchIfMissing = true)
    public QueryBus springQueryBus(List<QueryHandler<? extends Query<?>, ?>>  handlers) {
        return new SpringQueryBus(handlers);
    }

    @Bean
    @ConditionalOnProperty(name = "cqrs.logging.enabled", havingValue = "true")
    public CommandBus loggingCommandBus(CommandBus commandBus) {
        return new LoggingCommandBusDecorator(commandBus);
    }
}
