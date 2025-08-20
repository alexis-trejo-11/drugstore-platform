package microservice.user_service.users.core.application.command;

import lombok.Builder;
import microservice.user_service.users.core.domain.models.enums.Gender;

import java.util.Date;
import java.util.UUID;

@Builder
public record UpdateProfileCommand(
        UUID userId,
        String firstName,
        String lastName,
        Date dateOfBirth,
        Gender gender
) implements Command {
}
