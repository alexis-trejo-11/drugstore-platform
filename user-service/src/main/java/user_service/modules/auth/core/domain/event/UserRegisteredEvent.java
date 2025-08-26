package user_service.modules.auth.core.domain.event;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import lombok.Builder;
import lombok.Getter;

import user_service.modules.users.core.domain.models.enums.Gender;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {
    private final UUID userId;
    private final String firstName;
    private final String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private final String userType;

    @Builder
    public UserRegisteredEvent(Object source, UUID userId, String firstName, String lastName, LocalDate dateOfBirth,
            Gender gender, String userType) {
        super(source);
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.userType = userType;
    }

}
