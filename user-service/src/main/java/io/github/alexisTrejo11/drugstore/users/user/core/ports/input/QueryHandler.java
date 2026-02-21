package io.github.alexisTrejo11.drugstore.users.user.core.ports.input;

import io.github.alexisTrejo11.drugstore.users.user.core.application.queries.Query;

public interface QueryHandler<Q extends Query<R>, R> {
    R handle(Q query);
}
