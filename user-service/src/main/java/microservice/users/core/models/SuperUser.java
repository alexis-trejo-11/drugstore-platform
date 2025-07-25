package microservice.users.core.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microservice.users.core.models.enums.UserRole;
import microservice.users.core.models.valueobjects.Email;
import microservice.users.core.models.valueobjects.Password;
import microservice.users.core.models.valueobjects.UserId;


@Getter
public class SuperUser extends User {
    protected SuperUser(UserId id, Email email, Password password) {
        super(id, email, password, UserRole.ADMIN);
    }

}
