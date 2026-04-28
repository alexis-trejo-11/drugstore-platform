package org.github.alexisTrejo11.drugstore.stores.infrastructure.inbound.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import org.github.alexisTrejo11.drugstore.stores.application.port.in.command.CreateStoreCommand;
import org.github.alexisTrejo11.drugstore.stores.domain.model.enums.StoreStatus;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreCode;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreName;

@Schema(description = "Create Store Request DTO")
public record CreateStoreRequest(
        @NotNull @NotBlank
        @Schema(description = "Unique store exactCode", example = "STR-001")
        String code,

        @NotNull @NotBlank
        @Schema(description = "Store name", example = "Central Pharmacy")
        String name,

        @NotNull
        @Schema(description = "Store status", example = "ACTIVE")
        StoreStatus status,

        @NotNull
        @Schema(description = "Store contact information")
        StoreContactInfoRequest contactInfo,

        @NotNull @Valid
        @Schema(description = "Store address")
        AddressRequest address,

        @NotNull @Valid
        @Schema(description = "Store schedule")
        ScheduleInsertRequest schedule,

        @NotNull
        @Schema(description = "Geolocation of the store")
        GeolocationRequest geolocation


) {

    public CreateStoreCommand toCommand() {
        return new CreateStoreCommand(
                StoreCode.of(this.code),
                StoreName.of(this.name),
                this.status,
                this.address.toCommand(),
                this.contactInfo.toContactInfoCommand(),
                this.schedule.toScheduleCommand(),
                this.geolocation.toCommand()
        );
    }
};
