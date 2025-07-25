package microservice.users.core.models;

import lombok.Getter;
import microservice.users.core.models.enums.UserRole;
import microservice.users.core.models.valueobjects.Email;
import microservice.users.core.models.valueobjects.Password;
import microservice.users.core.models.valueobjects.UserId;


@Getter
public class Employee extends User {
    private String department;
    private String name;

    protected Employee(UserId id, Email email, Password password, UserRole role, String department, String name) {
        super(id, email, password, role);
        this.department = department;
        this.name = name;
    }
}
