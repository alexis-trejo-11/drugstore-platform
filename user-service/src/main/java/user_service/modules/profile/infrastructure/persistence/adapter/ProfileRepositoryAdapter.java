package user_service.modules.profile.infrastructure.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.profile.core.ports.output.ProfileRepository;
import user_service.modules.users.adapter.output.persistence.jpa.ProfileJpaRepository;
import user_service.modules.users.adapter.output.persistence.mappers.ModelMapper;
import user_service.modules.users.adapter.output.persistence.models.ProfileModel;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryAdapter implements ProfileRepository {
  private final ProfileJpaRepository profileJpaRepository;
  private final ModelMapper<Profile, ProfileModel> modelMapper;

  @Override
  public Profile save(Profile profile) {
    ProfileModel profileModel = modelMapper.fromEntity(profile);
    ProfileModel savedProfileModel = profileJpaRepository.save(profileModel);
    return modelMapper.toEntity(savedProfileModel);
  }

  @Override
  public Optional<Profile> findByUserId(UserId userId) {
    return profileJpaRepository.findByUserId(userId != null ? userId.value() : null)
        .map(modelMapper::toEntity);
  }

  @Override
  public void deleteByUserId(UserId userId) {
    ProfileModel profileModel = profileJpaRepository.findByUserId(userId != null ? userId.value() : null)
        .orElseThrow(() -> new RuntimeException("Profile not found"));

    profileJpaRepository.delete(profileModel);
  }

}
