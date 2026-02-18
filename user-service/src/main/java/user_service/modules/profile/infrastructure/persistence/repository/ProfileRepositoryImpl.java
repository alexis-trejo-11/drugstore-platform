package user_service.modules.profile.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.profile.core.ports.output.ProfileRepository;
import user_service.modules.profile.infrastructure.persistence.ProfileModelMapper;
import user_service.modules.users.adapter.output.persistence.jpa.ProfileJpaRepository;
import user_service.modules.users.adapter.output.persistence.models.ProfileModel;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

@Repository
public class ProfileRepositoryImpl implements ProfileRepository {
  private final ProfileJpaRepository profileJpaRepository;
  private final ProfileModelMapper modelMapper;

  @Autowired
  public ProfileRepositoryImpl(
      ProfileJpaRepository profileJpaRepository,
      ProfileModelMapper modelMapper) {
    this.profileJpaRepository = profileJpaRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public Profile save(Profile profile) {
    ProfileModel profileModel = modelMapper.fromEntity(profile);
    ProfileModel savedProfileModel = profileJpaRepository.save(profileModel);
    return modelMapper.toEntity(savedProfileModel);
  }

  @Override
  public Optional<Profile> findByUserId(UserId userId) {
    if (userId == null)
      return Optional.empty();

    return profileJpaRepository.findByUserId(userId.value())
        .map(modelMapper::toEntity);
  }

  @Override
  public void deleteByUserId(UserId userId) {
    if (userId == null)
      return;

    profileJpaRepository.deleteById(userId.value());
  }

}
