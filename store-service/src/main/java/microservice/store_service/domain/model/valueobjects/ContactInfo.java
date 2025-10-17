package microservice.store_service.domain.model.valueobjects;

import lombok.Builder;

@Builder
public record ContactInfo(
    String phone,
    String email
)
{}
