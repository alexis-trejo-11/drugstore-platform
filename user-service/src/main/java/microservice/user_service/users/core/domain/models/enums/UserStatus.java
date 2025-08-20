package microservice.user_service.users.core.domain.models.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    PENDING("Pending"),
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended"),
    DELETED("Deleted");

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

}
