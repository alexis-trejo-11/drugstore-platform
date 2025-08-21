package microservice.user_service.users.core.application.handlers.queries;

import microservice.user_service.users.core.application.dto.UserResponse;
import microservice.user_service.users.core.ports.input.QueryHandler;
import microservice.user_service.users.core.application.mappers.UserMapper;
import microservice.user_service.users.core.application.queries.GetUserByIdQuery;
import microservice.user_service.users.core.domain.exceptions.UserNotFoundError;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.ports.output.UserRepository;
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
