package user_service.config;

import user_service.modules.users.core.ports.input.CommandBus;
import user_service.modules.users.core.ports.input.QueryBus;
import user_service.modules.users.core.application.command.Command;
import user_service.modules.users.core.ports.input.CommandHandler;
import user_service.modules.users.core.ports.input.QueryHandler;
import user_service.modules.users.infrastructure.adapter.bus.SpringCommandBus;
import user_service.modules.users.infrastructure.adapter.bus.SpringQueryBus;
import user_service.modules.users.infrastructure.adapter.bus.decorator.LoggingCommandBusDecorator;
import user_service.modules.users.core.application.queries.Query;

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
    public QueryBus springQueryBus(List<QueryHandler<? extends Query<?>, ?>> handlers) {
        return new SpringQueryBus(handlers);
    }

    @Bean
    @ConditionalOnProperty(name = "cqrs.logging.enabled", havingValue = "true")
    public CommandBus loggingCommandBus(CommandBus commandBus) {
        return new LoggingCommandBusDecorator(commandBus);
    }
}
