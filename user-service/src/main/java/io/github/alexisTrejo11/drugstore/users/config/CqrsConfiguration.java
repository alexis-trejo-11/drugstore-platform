package io.github.alexisTrejo11.drugstore.users.config;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.github.alexisTrejo11.drugstore.users.app.user.adapter.input.bus.SpringCommandBus;
import io.github.alexisTrejo11.drugstore.users.app.user.adapter.input.bus.SpringQueryBus;
import io.github.alexisTrejo11.drugstore.users.app.user.adapter.input.bus.decorator.LoggingCommandBusDecorator;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.command.Command;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.queries.Query;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input.CommandBus;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input.CommandHandler;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input.QueryBus;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input.QueryHandler;

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
