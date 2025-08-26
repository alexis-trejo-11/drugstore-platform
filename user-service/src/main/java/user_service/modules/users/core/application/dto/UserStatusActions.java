package user_service.modules.users.core.application.dto;

import lombok.Getter;

@Getter
public enum UserStatusActions {
    ACTIVATE,
    DEACTIVATE,
    BAN,
    UNBAN,
}
