package user_service.modules.profile.core.application.dto;

import java.time.format.DateTimeFormatter;

import user_service.modules.profile.core.domain.model.Profile;

public record ProfileResponse(
                String firstName,
                String lastName,
                String dateOfBirth,
                String gender,
                String bio,
                String avatarUrl,
                String coverUrl

) {

        public static ProfileResponse from(
                        Profile profile) {
                return new ProfileResponse(
                                profile.getFirstName(),
                                profile.getLastName(),
                                profile.getDateOfBirth() != null
                                                ? profile.getDateOfBirth().format(DateTimeFormatter.BASIC_ISO_DATE)
                                                : null,
                                profile.getGender().name(),
                                profile.getBio(),
                                profile.getAvatarUrl(),
                                profile.getCoverPic());
        }
}
