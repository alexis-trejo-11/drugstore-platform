package microservice.auth_service.app.auth.core.application.events;

import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.auth_service.app.auth.core.domain.valueobjects.Email;
import microservice.auth_service.app.auth.core.domain.valueobjects.PhoneNumber;
import microservice.auth_service.app.auth.core.domain.valueobjects.UserId;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class UserRegisteredEvent {
    private String userId;
    private String email;
    private String phone;
    private LocalDateTime timestamp;

    public UserRegisteredEvent(UserId id, Email email, PhoneNumber phoneNumber) {
        this.userId = id.value().toString();
        this.email = email.value();
        this.phone = phoneNumber.value();
        this.timestamp = LocalDateTime.now();
    }
}


