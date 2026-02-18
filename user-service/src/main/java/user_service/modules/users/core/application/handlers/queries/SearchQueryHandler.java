package user_service.modules.users.core.application.handlers.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import user_service.modules.users.core.application.mappers.UserMapper;
import user_service.modules.users.core.application.queries.SearchUserQuery;
import user_service.modules.users.core.application.result.UserQueryResult;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.input.QueryHandler;
import user_service.modules.users.core.ports.output.UserRepository;

@Service
public class SearchQueryHandler implements QueryHandler<SearchUserQuery, Page<UserQueryResult>> {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public SearchQueryHandler(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public Page<UserQueryResult> handle(SearchUserQuery query) {
    Page<User> userPage = userRepository.search(query.toJson(), query.pageable());
    return userPage.map(userMapper::toUserQueryResult);
  }
}
