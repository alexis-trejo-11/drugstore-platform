package io.github.alexisTrejo11.drugstore.users.profile.core.domain.model;

import java.time.LocalDate;

import io.github.alexisTrejo11.drugstore.users.profile.core.domain.model.entities.CreateProfileParams;
import io.github.alexisTrejo11.drugstore.users.profile.core.domain.model.entities.ReconstructProfileParams;
import io.github.alexisTrejo11.drugstore.users.profile.core.domain.model.valueobjects.PersonalData;
import io.github.alexisTrejo11.drugstore.users.profile.core.domain.model.valueobjects.ProfileId;
import io.github.alexisTrejo11.drugstore.users.profile.core.domain.model.valueobjects.ProfileTimestamps;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.Gender;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.FullName;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;

public class Profile {
  private ProfileId id;
  private String bio;
  private String profilePictureUrl;
  private UserId userId;
  private PersonalData personalData;
  private ProfileTimestamps timestamps;

  private Profile() {
    // Initialize with default values to avoid NPEs
    this.personalData = PersonalData.NONE;
    this.timestamps = ProfileTimestamps.NONE;
    this.bio = "";
    this.profilePictureUrl = "";
  }

  /**
   * Creates a new profile with validation and business logic applied
   * 
   * @param params Parameters for creating a new profile
   * @return A new Profile instance with default values
   * @throws IllegalArgumentException if any validation fails
   */
  public static Profile createProfile(CreateProfileParams params) {
    if (params == null) {
      throw new IllegalArgumentException("CreateProfileParams cannot be null");
    }
    if (params.userId() == null) {
      throw new IllegalArgumentException("UserId is required");
    }
    if (params.personalData() == null) {
      throw new IllegalArgumentException("PersonalData is required");
    }

    Profile profile = new Profile();
    profile.id = ProfileId.generate();
    profile.userId = params.userId();
    profile.personalData = params.personalData();
    profile.bio = params.bio() != null ? params.bio() : "";
    profile.profilePictureUrl = params.profilePictureUrl() != null ? params.profilePictureUrl() : "";
    profile.timestamps = ProfileTimestamps.createNew();

    return profile;
  }

  /**
   * Reconstructs an existing profile from persistence data without validation
   * 
   * @param params Parameters containing all profile data from persistence
   * @return A Profile instance with all fields set from the parameters
   * @throws IllegalArgumentException if params is null
   */
  public static Profile reconstructProfile(ReconstructProfileParams params) {
    if (params == null) {
      throw new IllegalArgumentException("ReconstructProfileParams cannot be null");
    }

    Profile profile = new Profile();
    profile.id = params.id();
    profile.userId = params.userId();
    profile.personalData = params.personalData();
    profile.bio = params.bio();
    profile.profilePictureUrl = params.profilePictureUrl();
    profile.timestamps = params.timestamps();

    return profile;
  }

  public void updateProfileInfo(String bio, String profilePictureUrl) {
    this.bio = bio != null ? bio : this.bio;
    this.profilePictureUrl = profilePictureUrl != null ? profilePictureUrl : this.profilePictureUrl;
    this.timestamps = this.timestamps.updateLastModified();
  }

  public void updatePersonalInfo(FullName fullName, LocalDate dateOfBirth, Gender gender) {
    this.personalData.update(fullName, dateOfBirth, gender);
    this.timestamps = this.timestamps.updateLastModified();
  }

  public ProfileId getId() {
    return id;
  }

  public String getBio() {
    return bio;
  }

  public String getProfilePictureUrl() {
    return profilePictureUrl;
  }

  public UserId getUserId() {
    return userId;
  }

  public PersonalData getPersonalData() {
    return personalData;
  }

  public ProfileTimestamps getTimestamps() {
    return timestamps;
  }

  public String getFirstName() {
    if (personalData.fullName() == null) {
      return null;
    }

    return personalData.fullName().firstName();
  }

  public String getLastName() {
    if (personalData.fullName() == null) {
      return null;
    }
    return personalData.fullName().lastName();
  }

  public LocalDate getDateOfBirth() {
    return personalData != null ? personalData.dateOfBirth() : null;
  }

  public Gender getGender() {
    return personalData != null ? personalData.gender() : null;
  }

  public LocalDate getJoinedAt() {
    return timestamps != null ? timestamps.joinedAt() : null;
  }

  public LocalDate getLastProfileUpdateAt() {
    return timestamps != null ? timestamps.lastProfileUpdateAt() : null;
  }
}
