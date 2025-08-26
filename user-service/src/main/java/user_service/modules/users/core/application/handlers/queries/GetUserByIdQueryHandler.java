package user_service.modules.users.core.application.handlers.queries;

import user_service.modules.users.core.application.dto.UserResponse;
import user_service.modules.users.core.ports.input.QueryHandler;
import user_service.modules.users.core.application.mappers.UserMapper;
import user_service.modules.users.core.application.queries.GetUserByIdQuery;
import user_service.modules.users.core.domain.exceptions.UserNotFoundError;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.output.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetUserByIdQueryHandler implements QueryHandler<GetUserByIdQuery, UserResponse> {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public GetUserByIdQueryHandler(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse handle(GetUserByIdQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(() -> new UserNotFoundError(query.userId()));

        return userMapper.toResponse(user);
    }
}
