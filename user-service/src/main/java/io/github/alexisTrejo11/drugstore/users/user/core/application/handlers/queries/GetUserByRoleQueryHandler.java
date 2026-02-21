package io.github.alexisTrejo11.drugstore.users.user.core.application.handlers.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.users.user.core.application.mappers.UserMapper;
import io.github.alexisTrejo11.drugstore.users.user.core.application.queries.GetUsersByRoleQuery;
import io.github.alexisTrejo11.drugstore.users.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.input.QueryHandler;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.output.UserRepository;

@Service
public class GetUserByRoleQueryHandler implements QueryHandler<GetUsersByRoleQuery, Page<UserQueryResult>> {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public GetUserByRoleQueryHandler(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public Page<UserQueryResult> handle(GetUsersByRoleQuery query) {
    Page<User> userPage = userRepository.ListByRole(query.role(), query.pageInput());
    return userPage.map(userMapper::toUserQueryResult);
  }
}
