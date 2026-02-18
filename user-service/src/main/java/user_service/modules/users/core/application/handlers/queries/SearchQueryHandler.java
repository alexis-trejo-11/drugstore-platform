package user_service.modules.users.core.application.handlers.queries;

import user_service.modules.users.core.ports.input.QueryHandler;
import user_service.modules.users.core.application.mappers.UserMapper;
import user_service.modules.users.core.application.queries.SearchUserQuery;
import user_service.modules.users.core.application.result.UserPaginatedResponse;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.output.UserRepository;
import user_service.utils.page.PageResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchQueryHandler implements QueryHandler<SearchUserQuery, UserPaginatedResponse> {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public SearchQueryHandler(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public UserPaginatedResponse handle(SearchUserQuery query) {
    PageResponse<User> userPage = userRepository.search(query.toJson(), query.pageInput());
    return userMapper.toPaginatedResponse(userPage);
  }
}
