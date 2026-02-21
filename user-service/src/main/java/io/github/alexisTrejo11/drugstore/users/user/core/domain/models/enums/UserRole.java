package io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums;

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


		public static UserRole fromString(String roleName) {
			try {
				return UserRole.valueOf(roleName.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid role name: " + roleName);
			}
		}
}
