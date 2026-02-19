package io.github.alexisTrejo11.drugstore.users.app.user.core.application.handlers.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.users.app.user.core.application.mappers.UserMapper;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.queries.SearchUserQuery;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input.QueryHandler;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.output.UserRepository;

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
