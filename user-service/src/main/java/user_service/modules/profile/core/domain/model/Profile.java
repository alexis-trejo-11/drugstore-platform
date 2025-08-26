package user_service.modules.profile.core.domain.model;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import user_service.modules.users.core.domain.models.enums.Gender;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {
    // Basic profile information
    private UUID id;
    private String bio;
    private String avatarUrl;
    private String coverPic;

    // Links to other entities
    private UUID userId;
    private String customerId;
    private String employeeId;

    // Personal information
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;

    // Timestamps
    private LocalDate joinedAt;
    private LocalDate lastProfileUpdateAt;

    public static Profile CreateUserProfile(
            UUID userId,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            Gender gender) {
        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setBio("");
        profile.setAvatarUrl("default-avatar.png");
        profile.setCoverPic("default-cover.png");
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setDateOfBirth(dateOfBirth);
        profile.setGender(gender);
        return profile;
    }

    public void updateProfileInfo(String bio, String avatarUrl, String coverPic) {

        this.bio = bio != null ? bio : this.bio;
        this.avatarUrl = avatarUrl != null ? avatarUrl : this.avatarUrl;
        this.coverPic = coverPic != null ? coverPic : this.coverPic;
        this.lastProfileUpdateAt = LocalDate.now();
    }

    public void updatePersonalInfo(String firstName, String lastName, LocalDate dateOfBirth, Gender gender) {
        this.firstName = firstName != null ? firstName : this.firstName;
        this.lastName = lastName != null ? lastName : this.lastName;
        this.dateOfBirth = dateOfBirth != null ? dateOfBirth : this.dateOfBirth;
        this.gender = gender != null ? gender : this.gender;
        this.lastProfileUpdateAt = LocalDate.now();
    }
}
