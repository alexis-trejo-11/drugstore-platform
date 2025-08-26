package user_service.modules.users.core.domain.models.valueobjects;

import java.util.UUID;

public record EmployeeId(UUID value) {
    public EmployeeId {
        if (value == null) {
            throw new IllegalArgumentException("EmployeeId cannot be null");
        }
    }

    public static EmployeeId of(UUID value) {
        return new EmployeeId(value);
    }

    public static EmployeeId generate() {
        return new EmployeeId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
