package at.backend.drugstore.microservice.common_models.DTO.Order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class OrderPaymentDTO extends OrderDTO {

    @JsonProperty("client_name")
    private String clientName;

    @JsonProperty("client_phone")
    private String clientPhone;


}
