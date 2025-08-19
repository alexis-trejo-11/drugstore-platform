package microservice.users.core.application;

import microservice.users.core.application.queries.Query;

public interface QueryBus {
    <T> T execute(Query<T> query);
}
