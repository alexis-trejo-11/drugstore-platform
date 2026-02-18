package user_service.modules.users.core.application.handlers.queries;

import user_service.modules.users.core.ports.input.QueryHandler;
import user_service.modules.users.core.application.mappers.UserMapper;
import user_service.modules.users.core.application.queries.GetUserByPhoneNumberQuery;
import user_service.modules.users.core.application.result.UserResponse;
import user_service.modules.users.core.domain.exceptions.UserNotFoundError;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.output.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetUserPhoneQueryHandler implements QueryHandler<GetUserByPhoneNumberQuery, UserResponse> {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public GetUserPhoneQueryHandler(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public UserResponse handle(GetUserByPhoneNumberQuery query) {
    User user = userRepository.findByPhoneNumber(query.phoneNumber())
        .orElseThrow(() -> new UserNotFoundError(query.phoneNumber()));

    return userMapper.toResponse(user);
  }
}
