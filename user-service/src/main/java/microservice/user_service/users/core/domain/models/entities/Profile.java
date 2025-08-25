package microservice.user_service.users.core.domain.models.entities;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import microservice.user_service.users.core.domain.models.enums.Gender;

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

    public static Profile CreateCustomerUserProfile(
            UUID userId,
            String customerId,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            Gender gender) {
        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setBio("");
        profile.setAvatarUrl("default-avatar.png");
        profile.setCoverPic("default-cover.png");
        profile.setCustomerId(customerId);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setDateOfBirth(dateOfBirth);
        profile.setGender(gender);
        return profile;
    }

    public static Profile CreateEmployeeUserProfile(
            UUID userId,
            String employeeId,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            Gender gender) {
        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setBio("");
        profile.setAvatarUrl("default-avatar.png");
        profile.setCoverPic("default-cover.png");
        profile.setEmployeeId(employeeId);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setDateOfBirth(dateOfBirth);
        profile.setGender(gender);
        return profile;
    }
}
