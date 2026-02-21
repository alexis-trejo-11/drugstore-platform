package io.github.alexisTrejo11.drugstore.users.user.core.application.handlers.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.users.user.core.application.mappers.UserMapper;
import io.github.alexisTrejo11.drugstore.users.user.core.application.queries.GetUserByPhoneNumberQuery;
import io.github.alexisTrejo11.drugstore.users.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.exceptions.UserNotFoundError;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.input.QueryHandler;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.output.UserRepository;

@Service
public class GetUserPhoneQueryHandler implements QueryHandler<GetUserByPhoneNumberQuery, UserQueryResult> {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public GetUserPhoneQueryHandler(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public UserQueryResult handle(GetUserByPhoneNumberQuery query) {
    User user = userRepository.findByPhoneNumber(query.phoneNumber())
        .orElseThrow(() -> new UserNotFoundError(query.phoneNumber()));

    return userMapper.toUserQueryResult(user);
  }
}
