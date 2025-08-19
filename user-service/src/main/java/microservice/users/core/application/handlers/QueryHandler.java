package microservice.users.core.application.handlers;

import microservice.users.core.application.queries.Query;

public interface QueryHandler<Q extends Query<R>, R> {
    R handle(Q query);
}

