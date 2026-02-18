package user_service.modules.users.core.application.handlers.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import user_service.modules.users.core.application.mappers.UserMapper;
import user_service.modules.users.core.application.queries.ListUserByRoleQuery;
import user_service.modules.users.core.application.result.UserQueryResult;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.input.QueryHandler;
import user_service.modules.users.core.ports.output.UserRepository;

@Service
public class GetUserByRoleQueryHandler implements QueryHandler<ListUserByRoleQuery, Page<UserQueryResult>> {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public GetUserByRoleQueryHandler(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public Page<UserQueryResult> handle(ListUserByRoleQuery query) {
    Page<User> userPage = userRepository.ListByRole(query.role(), query.pageInput());
    return userPage.map(userMapper::toUserQueryResult);
  }
}
