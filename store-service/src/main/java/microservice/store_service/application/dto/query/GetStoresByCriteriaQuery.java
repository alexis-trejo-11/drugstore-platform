package microservice.store_service.application.dto.query;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.codehaus.commons.nullanalysis.NotNull;

@Builder
public record GetStoresByCriteriaQuery() {}