package microservice.order_service.orders.application.commands.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateDeliveryAddressCommand {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String additionalInfo;
}
