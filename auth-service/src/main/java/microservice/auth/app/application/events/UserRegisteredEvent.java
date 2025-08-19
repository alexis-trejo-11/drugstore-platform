package microservice.auth.app.application.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import microservice.auth.app.core.valueobjects.Email;
import microservice.auth.app.core.valueobjects.PhoneNumber;
import microservice.auth.app.core.valueobjects.UserId;

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


