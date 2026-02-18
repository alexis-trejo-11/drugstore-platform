package user_service.modules.users.core.application.result;

import lombok.Getter;

@Getter
public enum UserStatusActions {
    ACTIVATE,
    DEACTIVATE,
    BAN,
    UNBAN,
}
