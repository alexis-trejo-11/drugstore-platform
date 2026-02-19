package io.github.alexisTrejo11.drugstore.users.app.user.core.application.handlers.queries;

import io.github.alexisTrejo11.drugstore.users.app.user.core.application.mappers.UserMapper;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.queries.GetUserByIdQuery;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.exceptions.UserNotFoundError;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input.QueryHandler;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.output.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetUserByIdQueryHandler implements QueryHandler<GetUserByIdQuery, UserQueryResult> {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public GetUserByIdQueryHandler(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public UserQueryResult handle(GetUserByIdQuery query) {
    User user = userRepository.findById(query.userId())
        .orElseThrow(() -> new UserNotFoundError(query.userId()));

    return userMapper.toUserQueryResult(user);
  }
}
