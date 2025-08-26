package user_service.modules.users.infrastructure.adapter.persistence.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.infrastructure.adapter.persistence.models.UserModel;
import user_service.utils.page.PageMapper;
import user_service.utils.page.PageResponse;

@Component
@RequiredArgsConstructor
public class UserModelMapper implements ModelMapper<User, UserModel> {
    private final PageMapper pageMapper;

    @Override
    public UserModel fromEntity(User entity) {
        if (entity == null) {
            return null;
        }
        UserModel model = new UserModel();
        model.setId(entity.getId());
        model.setEmail(entity.getEmail());
        model.setPhoneNumber(entity.getPhoneNumber());
        model.setHashedPassword(entity.getHashedPassword());
        model.setStatus(entity.getStatus());
        model.setRole(entity.getRole());
        model.setTwoFactorId(entity.getTwoFactorId());
        model.setLastLoginAt(entity.getLastLoginAt());
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());
        return model;
    }

    @Override
    public User toEntity(UserModel model) {
        if (model == null) {
            return null;
        }
        User entity = new User();
        entity.setId(model.getId());
        entity.setEmail(model.getEmail());
        entity.setPhoneNumber(model.getPhoneNumber());
        entity.setHashedPassword(model.getHashedPassword());
        entity.setStatus(model.getStatus());
        entity.setRole(model.getRole());
        entity.setTwoFactorId(model.getTwoFactorId());
        entity.setLastLoginAt(model.getLastLoginAt());
        entity.setCreatedAt(model.getCreatedAt());
        entity.setUpdatedAt(model.getUpdatedAt());
        return entity;
    }

    @Override
    public List<UserModel> fromEntities(List<User> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream()
                .map(this::fromEntity)
                .toList();
    }

    @Override
    public List<User> toEntities(List<UserModel> models) {
        if (models == null || models.isEmpty()) {
            return List.of();
        }
        return models.stream()
                .map(this::toEntity)
                .toList();
    }

    @Override
    public PageResponse<User> toPageResponse(Page<UserModel> modelPage) {
        if (modelPage == null) {
            return PageResponse.empty();
        }
        Page<User> userPage = modelPage.map(this::toEntity);
        return pageMapper.toPageResponse(userPage);
    }
}
