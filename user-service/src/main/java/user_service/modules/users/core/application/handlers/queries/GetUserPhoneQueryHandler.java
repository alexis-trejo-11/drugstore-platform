package user_service.modules.users.core.application.handlers.queries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user_service.modules.users.core.application.mappers.UserMapper;
import user_service.modules.users.core.application.queries.GetUserByPhoneNumberQuery;
import user_service.modules.users.core.application.result.UserQueryResult;
import user_service.modules.users.core.domain.exceptions.UserNotFoundError;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.input.QueryHandler;
import user_service.modules.users.core.ports.output.UserRepository;

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
