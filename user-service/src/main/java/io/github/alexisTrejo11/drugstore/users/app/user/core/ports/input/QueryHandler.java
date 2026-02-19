package io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input;

import io.github.alexisTrejo11.drugstore.users.app.user.core.application.queries.Query;

public interface QueryHandler<Q extends Query<R>, R> {
    R handle(Q query);
}
