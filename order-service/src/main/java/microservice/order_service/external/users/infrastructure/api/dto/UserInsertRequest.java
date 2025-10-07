package microservice.order_service.external.users.infrastructure.api.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import microservice.order_service.external.users.application.command.CreateUserCommand;
import microservice.order_service.external.users.application.command.UpdateUserCommand;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.hibernate.validator.constraints.Length;

@Builder
public record UserInsertRequest(
        @NotNull @NotEmpty @Length(min = 7, max = 100)
        String name,
        @NotNull @NotEmpty @Email
        String email,
        @NotEmpty @Length(min = 8, max = 15)
        String phoneNumber,
        @NotEmpty @Length(min = 3, max = 20) @NotNull
        String role,
        @NotNull @NotBlank @Length(min = 3, max = 20)
        String status
) {

    public CreateUserCommand toCommand() {
        return CreateUserCommand.builder()
                .name(this.name)
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .role(this.role)
                .status(this.status)
                .build();
    }

    public UpdateUserCommand toCommand(String id) {
        return UpdateUserCommand.builder()
                .id(UserID.of(id))
                .name(this.name)
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .role(this.role)
                .status(this.status)
                .build();
    }
}
