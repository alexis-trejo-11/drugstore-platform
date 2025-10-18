package microservice.store_service.infrastructure.adapter.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import microservice.store_service.application.command.valueobject.ContactInfoCommand;

public record StoreContactInfoRequest(
        @NotNull @NotBlank
        @Schema(description = "Contact phone", example = "+123456789")
        String phone,

        @NotNull @NotBlank @Email
        @Schema(description = "Contact email", example = "store@example.com")
        String email
) {
    public ContactInfoCommand toContactInfoCommand() {
        return new ContactInfoCommand(this.phone, this.email);
    }
}
