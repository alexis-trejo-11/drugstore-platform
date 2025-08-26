package user_service.modules.auth.core.domain.event;

import lombok.Builder;
import lombok.Getter;
import user_service.modules.users.core.domain.models.enums.Gender;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.context.ApplicationEvent;

@Getter
public class CreateProfileEvent extends ApplicationEvent {
    private final UUID userId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private final String userType;

    @Builder
    public CreateProfileEvent(
            UUID userId,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            Gender gender,
            String userType) {
        super(userId);
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.userType = userType;
    }
}
