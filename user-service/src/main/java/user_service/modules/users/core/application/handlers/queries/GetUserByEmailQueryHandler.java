package user_service.modules.users.core.application.handlers.queries;

import user_service.modules.users.core.ports.input.QueryHandler;
import user_service.modules.users.core.application.mappers.UserMapper;
import user_service.modules.users.core.application.queries.GetUserByEmailQuery;
import user_service.modules.users.core.application.result.UserResponse;
import user_service.modules.users.core.domain.exceptions.UserNotFoundError;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.output.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetUserByEmailQueryHandler implements QueryHandler<GetUserByEmailQuery, UserResponse> {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public GetUserByEmailQueryHandler(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public UserResponse handle(GetUserByEmailQuery query) {
    User user = userRepository.findByEmail(query.email())
        .orElseThrow(() -> new UserNotFoundError(query.email()));

    return userMapper.toResponse(user);
  }
}
