package user_service.modules.users.core.ports.input;

import user_service.modules.users.core.application.queries.Query;

public interface QueryHandler<Q extends Query<R>, R> {
    R handle(Q query);
}
