package user_service.modules.profile.core.domain.model;

import java.time.LocalDate;

import user_service.modules.profile.core.domain.model.entities.CreateProfileParams;
import user_service.modules.profile.core.domain.model.entities.ReconstructProfileParams;
import user_service.modules.profile.core.domain.model.valueobjects.PersonalData;
import user_service.modules.profile.core.domain.model.valueobjects.ProfileId;
import user_service.modules.profile.core.domain.model.valueobjects.ProfileTimestamps;
import user_service.modules.users.core.domain.models.enums.Gender;
import user_service.modules.users.core.domain.models.valueobjects.FullName;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

public class Profile {
  private ProfileId id;
  private String bio;
  private String avatarUrl;
  private String coverPic;
  private UserId userId;
  private PersonalData personalData;
  private ProfileTimestamps timestamps;

  private Profile() {
    // Initialize with default values to avoid NPEs
    this.personalData = PersonalData.NONE;
    this.timestamps = ProfileTimestamps.NONE;
    this.bio = "";
    this.avatarUrl = "";
    this.coverPic = "";
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
    profile.avatarUrl = params.avatarUrl() != null ? params.avatarUrl() : "";
    profile.coverPic = params.coverPic() != null ? params.coverPic() : "";
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
    profile.avatarUrl = params.avatarUrl();
    profile.coverPic = params.coverPic();
    profile.timestamps = params.timestamps();

    return profile;
  }

  public void updateProfileInfo(String bio, String avatarUrl, String coverPic) {
    this.bio = bio != null ? bio : this.bio;
    this.avatarUrl = avatarUrl != null ? avatarUrl : this.avatarUrl;
    this.coverPic = coverPic != null ? coverPic : this.coverPic;
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

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public String getCoverPic() {
    return coverPic;
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
