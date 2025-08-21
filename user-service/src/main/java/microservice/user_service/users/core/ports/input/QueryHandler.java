package microservice.user_service.users.core.ports.input;

import microservice.user_service.users.core.application.queries.Query;

public interface QueryHandler<Q extends Query<R>, R> {
    R handle(Q query);
}

