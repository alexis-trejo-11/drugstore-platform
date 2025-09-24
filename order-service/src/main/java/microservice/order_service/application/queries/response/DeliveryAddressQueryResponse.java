package microservice.order_service.application.queries.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryAddressQueryResponse {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String additionalInfo;
}

