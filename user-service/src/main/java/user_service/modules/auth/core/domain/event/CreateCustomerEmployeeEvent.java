package user_service.modules.auth.core.domain.event;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class CreateCustomerEmployeeEvent extends ApplicationEvent {
    private final UUID userId;
    private final String userType;
    private final String email;
    private final String firstName;
    private final String lastName;

    public CreateCustomerEmployeeEvent(Object source, UUID userId, String userType,
            String email, String firstName, String lastName) {
        super(source);
        this.userId = userId;
        this.userType = userType;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
