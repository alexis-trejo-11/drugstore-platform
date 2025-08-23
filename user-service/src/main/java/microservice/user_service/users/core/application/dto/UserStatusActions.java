package microservice.user_service.users.core.application.dto;

import lombok.Getter;

@Getter
public enum UserStatusActions {
    ACTIVATE,
    DEACTIVATE,
    BAN,
    UNBAN,
}
