package user_service.modules.users.core.application.handlers.queries;

import user_service.modules.users.core.application.dto.UserPaginatedResponse;
import user_service.modules.users.core.ports.input.QueryHandler;
import user_service.modules.users.core.application.mappers.UserMapper;
import user_service.modules.users.core.application.queries.ListUserByRoleQuery;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.output.UserRepository;
import user_service.utils.page.PageResponse;
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
