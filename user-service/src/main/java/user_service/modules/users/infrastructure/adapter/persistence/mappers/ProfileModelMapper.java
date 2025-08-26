package user_service.modules.users.infrastructure.adapter.persistence.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.users.infrastructure.adapter.persistence.models.ProfileModel;
import user_service.utils.page.PageMapper;
import user_service.utils.page.PageResponse;

@Component
@RequiredArgsConstructor
public class ProfileModelMapper implements ModelMapper<Profile, ProfileModel> {
    private final PageMapper pageMapper;

    @Override
    public ProfileModel fromEntity(Profile entity) {
        return ProfileModel.builder()
                .id(entity.getId())
                .avatarUrl(entity.getAvatarUrl())
                .bio(entity.getBio())
                .coverPic(entity.getCoverPic())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .userId(entity.getUserId())
                .dateOfBirth(entity.getDateOfBirth())
                .gender(entity.getGender())
                .build();
    }

    @Override
    public Profile toEntity(ProfileModel model) {
        return Profile.builder()
                .id(model.getId())
                .avatarUrl(model.getAvatarUrl())
                .bio(model.getBio())
                .coverPic(model.getCoverPic())
                .firstName(model.getFirstName())
                .lastName(model.getLastName())
                .userId(model.getUserId())
                .dateOfBirth(model.getDateOfBirth())
                .build();
    }

    @Override
    public List<ProfileModel> fromEntities(List<Profile> entities) {
        return entities.stream().map(this::fromEntity).toList();
    }

    @Override
    public List<Profile> toEntities(List<ProfileModel> models) {
        return models.stream().map(this::toEntity).toList();
    }

    @Override
    public PageResponse<Profile> toPageResponse(Page<ProfileModel> modelPage) {
        Page<Profile> profilePage = modelPage.map(this::toEntity);
        return pageMapper.toPageResponse(profilePage);
    }

}
