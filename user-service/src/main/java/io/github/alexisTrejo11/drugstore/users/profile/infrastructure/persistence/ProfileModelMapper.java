package io.github.alexisTrejo11.drugstore.users.profile.infrastructure.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import libs_kernel.page.PageResponse;
import io.github.alexisTrejo11.drugstore.users.profile.core.domain.model.Profile;
import io.github.alexisTrejo11.drugstore.users.profile.core.domain.model.entities.ReconstructProfileParams;
import io.github.alexisTrejo11.drugstore.users.profile.core.domain.model.valueobjects.PersonalData;
import io.github.alexisTrejo11.drugstore.users.profile.core.domain.model.valueobjects.ProfileId;
import io.github.alexisTrejo11.drugstore.users.profile.core.domain.model.valueobjects.ProfileTimestamps;
import io.github.alexisTrejo11.drugstore.users.user.adapter.output.persistence.models.ProfileModel;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.FullName;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;

@Component
public class ProfileModelMapper {

  public ProfileModel fromEntity(Profile entity) {
    return ProfileModel.builder()
        .id(entity.getId() != null ? entity.getId().value() : null)
        .profilePictureUrl(entity.getProfilePictureUrl())
        .bio(entity.getBio())
        .firstName(entity.getFirstName())
        .lastName(entity.getLastName())
        .userId(entity.getUserId() != null ? entity.getUserId().value() : null)
        .dateOfBirth(entity.getDateOfBirth())
        .gender(entity.getGender())
        .createdAt(entity.getJoinedAt() != null ? entity.getJoinedAt().atStartOfDay() : null)
        .updatedAt(entity.getLastProfileUpdateAt() != null ? entity.getLastProfileUpdateAt().atStartOfDay() : null)
        .build();
  }

  public Profile toEntity(ProfileModel model) {
    if (model == null) {
      return null;
    }

    FullName fullName = null;
    if (model.getFirstName() != null || model.getLastName() != null) {
      fullName = new FullName(model.getFirstName(), model.getLastName());
    }

    PersonalData personalData = new PersonalData(
        fullName,
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
        .profilePictureUrl(model.getProfilePictureUrl())
        .timestamps(timestamps)
        .build();

    return Profile.reconstructProfile(params);
  }

  public List<ProfileModel> fromEntities(List<Profile> entities) {
    return entities.stream().map(this::fromEntity).toList();
  }

  public List<Profile> toEntities(List<ProfileModel> models) {
    return models.stream().map(this::toEntity).toList();
  }

  public PageResponse<Profile> toPageResponse(Page<ProfileModel> modelPage) {
    Page<Profile> profilePage = modelPage.map(this::toEntity);
    return PageResponse.from(profilePage);
  }

}
