package microservice.order_service.external.users.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import microservice.order_service.external.users.application.command.CreateUserCommand;
import microservice.order_service.external.users.application.command.UpdateUserCommand;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.hibernate.validator.constraints.Length;

@Builder
@Schema(description = "Request DTO for creating or updating a userID")
public record UserInsertRequest(
        @Schema(
                description = "Full name of the userID",
                example = "Carol White",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 7,
                maxLength = 100,
                pattern = "^[a-zA-Z\\s]+$"
        )
        @NotNull @NotEmpty @Length(min = 7, max = 100)
        String name,

        @Schema(
                description = "Email address of the userID",
                example = "carol.white@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "email",
                pattern = "^[A-Za-z0-9+_.-]+@(.+)$"
        )
        @NotNull @NotEmpty @Email
        String email,

        @Schema(
                description = "Phone number of the userID",
                example = "+1234567890",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 8,
                maxLength = 15,
                pattern = "^\\+?[\\d\\s-()]+$"
        )
        @JsonProperty("phone_number")
        @NotEmpty @Length(min = 8, max = 15)
        String phoneNumber,

        @Schema(
                description = "Role assigned to the userID",
                example = "CUSTOMER",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 3,
                maxLength = 20,
                allowableValues = {"ADMIN", "CUSTOMER", "MANAGER", "EMPLOYEE", "VENDOR"}
        )
        @NotEmpty @Length(min = 3, max = 20) @NotNull
        String role,

        @Schema(
                description = "Current status of the userID account",
                example = "ACTIVE",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 3,
                maxLength = 20,
                allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED", "PENDING", "DELETED"}
        )
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