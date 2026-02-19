package io.github.alexisTrejo11.drugstore.users.app.profile.core.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.users.app.profile.core.application.dto.CreateProfileCommand;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.exception.UserProfileAlreadyExistsError;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.Profile;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.entities.CreateProfileParams;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.valueobjects.PersonalData;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.ports.output.ProfileRepository;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.entities.User;

@Service
public class CreateUserProfileUseCase {
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CreateUserProfileUseCase.class);
  private final ProfileRepository profileRepository;

  @Autowired
  public CreateUserProfileUseCase(ProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
  }

  public void execute(User user, CreateProfileCommand createProfile) {
    logger.info("Creating profile for user with ID: {}", user.getId());

    profileRepository.findByUserId(user.getId()).ifPresent(existingProfile -> {
      throw new UserProfileAlreadyExistsError(user.getId());
    });

    logger.info("No existing profile found for user ID: {}. Proceeding to create a new profile.", user.getId());

    PersonalData personalData = new PersonalData(
        createProfile.fullName() != null ? createProfile.fullName() : null,
        createProfile.dateOfBirth(),
        createProfile.gender());

    CreateProfileParams params = new CreateProfileParams(
        user.getId(),
        personalData,
        createProfile.bio(),
        createProfile.profilePictureUrl());

    Profile profile = Profile.createProfile(params);

    logger.info("Saving new profile for user ID: {}", user.getId());
    profileRepository.save(profile);

    logger.info("Profile created successfully for user ID: {}", user.getId());
  }
}
