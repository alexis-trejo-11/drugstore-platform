package microservice.user_service.users.core.ports.input;

import microservice.user_service.users.core.application.queries.Query;

public interface QueryBus {
    <T> T execute(Query<T> query);
}
