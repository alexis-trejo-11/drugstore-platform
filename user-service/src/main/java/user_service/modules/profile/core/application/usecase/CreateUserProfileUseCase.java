package user_service.modules.profile.core.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user_service.modules.profile.core.application.dto.CreateProfileCommand;
import user_service.modules.profile.core.domain.exception.UserProfileAlreadyExistsError;
import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.profile.core.domain.model.entities.CreateProfileParams;
import user_service.modules.profile.core.domain.model.valueobjects.PersonalData;
import user_service.modules.profile.core.ports.output.ProfileRepository;
import user_service.modules.users.core.domain.models.entities.User;

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
        createProfile.avatarUrl(),
        createProfile.coverUrl());

    Profile profile = Profile.createProfile(params);

    logger.info("Saving new profile for user ID: {}", user.getId());
    profileRepository.save(profile);

    logger.info("Profile created successfully for user ID: {}", user.getId());
  }
}
