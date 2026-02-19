package io.github.alexisTrejo11.drugstore.users.app.user.core.application.handlers.queries;

import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input.QueryHandler;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.mappers.UserMapper;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.queries.GetUserByStatusQuery;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.output.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class GetUserByRoleStatusHandler implements QueryHandler<GetUserByStatusQuery, Page<UserQueryResult>> {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public GetUserByRoleStatusHandler(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public Page<UserQueryResult> handle(GetUserByStatusQuery query) {
    Page<User> userPage = userRepository.ListByStatus(query.status(), query.pageInput());
    return userPage.map(userMapper::toUserQueryResult);
  }
}