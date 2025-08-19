package microservice.users.core.application.command;

import lombok.Builder;
import microservice.users.core.domain.models.enums.Gender;

import java.util.Date;

@Builder
public record UpdateProfileCommand(
        String userId,
        String firstName,
        String lastName,
        Date dateOfBirth,
        Gender gender
) implements Command {
}
