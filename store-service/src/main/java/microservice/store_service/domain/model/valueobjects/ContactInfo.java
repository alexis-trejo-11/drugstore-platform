package microservice.store_service.domain.model.valueobjects;

import lombok.Builder;

@Builder
public record ContactInfo(
    String phone,
    String email
)
{
    public static ContactInfo of(String phone, String email) {
        return new ContactInfo(phone, email);
    }

}