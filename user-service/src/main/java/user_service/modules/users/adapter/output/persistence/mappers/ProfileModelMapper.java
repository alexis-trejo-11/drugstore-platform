package user_service.modules.users.adapter.output.persistence.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.profile.core.domain.model.entities.ReconstructProfileParams;
import user_service.modules.profile.core.domain.model.valueobjects.PersonalData;
import user_service.modules.profile.core.domain.model.valueobjects.ProfileId;
import user_service.modules.profile.core.domain.model.valueobjects.ProfileTimestamps;
import user_service.modules.users.adapter.output.persistence.models.ProfileModel;
import user_service.modules.users.core.domain.models.valueobjects.UserId;
import user_service.utils.page.PageMapper;
import user_service.utils.page.PageResponse;

@Component
@RequiredArgsConstructor
public class ProfileModelMapper implements ModelMapper<Profile, ProfileModel> {
  private final PageMapper pageMapper;

  @Override
  public ProfileModel fromEntity(Profile entity) {
    return ProfileModel.builder()
        .id(entity.getId() != null ? entity.getId().value() : null)
        .avatarUrl(entity.getAvatarUrl())
        .bio(entity.getBio())
        .coverPic(entity.getCoverPic())
        .firstName(entity.getFirstName())
        .lastName(entity.getLastName())
        .userId(entity.getUserId() != null ? entity.getUserId().value() : null)
        .dateOfBirth(entity.getDateOfBirth())
        .gender(entity.getGender())
        .createdAt(entity.getJoinedAt() != null ? entity.getJoinedAt().atStartOfDay() : null)
        .updatedAt(entity.getLastProfileUpdateAt() != null ? entity.getLastProfileUpdateAt().atStartOfDay() : null)
        .build();
  }

  @Override
  public Profile toEntity(ProfileModel model) {
    if (model == null) {
      return null;
    }

    PersonalData personalData = new PersonalData(
        model.getFirstName(),
        model.getLastName(),
        model.getDateOfBirth(),
        model.getGender());

    ProfileTimestamps timestamps = new ProfileTimestamps(
        model.getCreatedAt() != null ? model.getCreatedAt().toLocalDate() : null,
        model.getUpdatedAt() != null ? model.getUpdatedAt().toLocalDate() : null);

    ReconstructProfileParams params = ReconstructProfileParams.builder()
        .id(model.getId() != null ? ProfileId.of(model.getId()) : null)
        .userId(model.getUserId() != null ? new UserId(model.getUserId()) : null)
        .personalData(personalData)
        .bio(model.getBio())
        .avatarUrl(model.getAvatarUrl())
        .coverPic(model.getCoverPic())
        .timestamps(timestamps)
        .build();

    return Profile.reconstructProfile(params);
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
