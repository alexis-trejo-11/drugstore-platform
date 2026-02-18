package user_service.modules.profile.core.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user_service.modules.profile.core.application.dto.CreateProfileCommand;
import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.profile.core.domain.model.entities.CreateProfileParams;
import user_service.modules.profile.core.domain.model.valueobjects.PersonalData;
import user_service.modules.profile.core.ports.output.ProfileRepository;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.utils.exceptions.ConflictException;

@Service
public class CreateUserProfileUseCase {
  private final ProfileRepository profileRepository;

  @Autowired
  public CreateUserProfileUseCase(ProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
  }

  public void execute(User user, CreateProfileCommand createProfile) {
    profileRepository.findByUserId(user.getId()).ifPresent(existingProfile -> {
      throw new ConflictException("Profile already exists for user with id: " + user.getId().toString(),
          "PROFILE_ALREADY_EXISTS");
    });

    PersonalData personalData = new PersonalData(
        createProfile.fullName() != null ? createProfile.fullName().firstName() : null,
        createProfile.fullName() != null ? createProfile.fullName().lastName() : null,
        createProfile.dateOfBirth(),
        createProfile.gender());

    CreateProfileParams params = new CreateProfileParams(
        user.getId(),
        personalData,
        createProfile.bio(),
        createProfile.avatarUrl(),
        createProfile.coverUrl());

    Profile profile = Profile.createProfile(params);
    profileRepository.save(profile);
  }
}
