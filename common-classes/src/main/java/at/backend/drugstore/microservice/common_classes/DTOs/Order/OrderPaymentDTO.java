package at.backend.drugstore.microservice.common_classes.DTOs.Order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class OrderPaymentDTO extends OrderDTO {

    @JsonProperty("client_name")
    private String clientName;

    @JsonProperty("client_phone")
    private String clientPhone;


}
