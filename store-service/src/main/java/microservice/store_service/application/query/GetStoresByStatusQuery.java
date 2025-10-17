package microservice.store_service.application.query;

import jakarta.validation.constraints.NotBlank;
import org.codehaus.commons.nullanalysis.NotNull;

public record GetStoresByStateQuery(@NotNull @NotBlank String state) {}
