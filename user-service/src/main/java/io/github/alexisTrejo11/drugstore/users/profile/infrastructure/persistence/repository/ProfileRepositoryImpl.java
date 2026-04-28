package io.github.alexisTrejo11.drugstore.users.profile.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.alexisTrejo11.drugstore.users.profile.core.domain.model.Profile;
import io.github.alexisTrejo11.drugstore.users.profile.core.ports.output.ProfileRepository;
import io.github.alexisTrejo11.drugstore.users.profile.infrastructure.persistence.ProfileModelMapper;
import io.github.alexisTrejo11.drugstore.users.user.adapter.output.persistence.jpa.ProfileJpaRepository;
import io.github.alexisTrejo11.drugstore.users.user.adapter.output.persistence.models.ProfileModel;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;

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
