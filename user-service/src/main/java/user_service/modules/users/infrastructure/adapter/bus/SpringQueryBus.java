package user_service.modules.users.infrastructure.adapter.bus;

import jakarta.annotation.PostConstruct;
import user_service.modules.users.core.ports.input.QueryBus;
import user_service.modules.users.core.ports.input.QueryHandler;
import user_service.modules.users.core.application.queries.Query;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpringQueryBus implements QueryBus {

    private final Map<Class<? extends Query<?>>, QueryHandler<? extends Query<?>, ?>> handlers = new HashMap<>();
    private final List<QueryHandler<? extends Query<?>, ?>> queryHandlers;

    @Autowired
    public SpringQueryBus(List<QueryHandler<? extends Query<?>, ?>> queryHandlers) {
        this.queryHandlers = queryHandlers;
    }

    @PostConstruct
    public void initializeHandlers() {
        queryHandlers.forEach(handler -> {
            Class<? extends Query<?>> queryType = getQueryType(handler);
            if (handlers.containsKey(queryType)) {
                throw new IllegalStateException(
                        "Multiple handlers found for query: " + queryType.getSimpleName());
            }
            handlers.put(queryType, handler);
            System.out.println("Registered query handler: " + handler.getClass().getSimpleName() +
                    " for query: " + queryType.getSimpleName());
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(Query<T> query) {
        QueryHandler<Query<T>, T> handler = (QueryHandler<Query<T>, T>) handlers.get(query.getClass());
        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler registered for query: " + query.getClass().getSimpleName());
        }

        return handler.handle(query);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Query<?>> getQueryType(QueryHandler<? extends Query<?>, ?> handler) {
        Type[] genericInterfaces = handler.getClass().getGenericInterfaces();

        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                if (parameterizedType.getRawType().equals(QueryHandler.class)) {
                    return (Class<? extends Query<?>>) parameterizedType.getActualTypeArguments()[0];
                }
            }
        }

        throw new IllegalArgumentException(
                "Could not determine query type for handler: " + handler.getClass().getSimpleName());
    }

    public Map<Class<? extends Query<?>>, QueryHandler<? extends Query<?>, ?>> getRegisteredHandlers() {
        return new HashMap<>(handlers);
    }
}