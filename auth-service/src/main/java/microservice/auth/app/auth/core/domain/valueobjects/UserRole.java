package microservice.auth.app.auth.core.domain.valueobjects;

import lombok.Getter;

import java.util.Set;

@Getter
public enum UserRole {
    CUSTOMER("customer", Set.of("READ_PROFILE", "MANAGE_CART")),
    EMPLOYEE("employee", Set.of("MANAGE_INVENTORY", "VIEW_REPORTS")),
    ADMIN("admin", Set.of("MANAGE_USERS", "ALL_PERMISSIONS"));

    private final String roleName;
    private final Set<String> permissions;

    UserRole(String roleName, Set<String> permissions) {
        this.roleName = roleName;
        this.permissions = permissions;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

}
