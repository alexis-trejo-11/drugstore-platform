package microservice.user_service.users.core.application.handlers.queries;

import microservice.user_service.users.core.application.dto.UserResponse;
import microservice.user_service.users.core.ports.input.QueryHandler;
import microservice.user_service.users.core.application.mappers.UserMapper;
import microservice.user_service.users.core.application.queries.GetUserByEmailQuery;
import microservice.user_service.users.core.domain.exceptions.UserNotFoundError;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.ports.output.UserRepository;
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
