package microservice.user_service.users.core.application.handlers.queries;

import microservice.user_service.users.core.application.dto.UserPaginatedResponse;
import microservice.user_service.users.core.ports.input.QueryHandler;
import microservice.user_service.users.core.application.mappers.UserMapper;
import microservice.user_service.users.core.application.queries.ListUserByRoleQuery;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.ports.output.UserRepository;
import microservice.user_service.utils.page.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetUserByRoleQueryHandler implements QueryHandler<ListUserByRoleQuery, UserPaginatedResponse> {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public GetUserByRoleQueryHandler(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserPaginatedResponse handle(ListUserByRoleQuery query) {
        PageResponse<User> userPage = userRepository.ListByRole(query.role(), query.pageInput());
        return userMapper.toPaginatedResponse(userPage);
    }
}
