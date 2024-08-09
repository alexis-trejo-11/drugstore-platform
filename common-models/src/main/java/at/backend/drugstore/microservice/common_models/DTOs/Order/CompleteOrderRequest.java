package at.backend.drugstore.microservice.common_models.DTOs.Order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteOrderRequest {
    @JsonProperty("is_order_paid")
    @NotNull(message = "is_order_paid is obligatory")
    private boolean isOrderPaid;

    @JsonProperty("order_id")
    @NotNull(message = "order_id is obligatory")
    @Positive(message = "order_id must be positive")
    private Long orderId;

    @JsonProperty("address_id")
    private Long addressId;

    @JsonProperty("client_id")
    private Long clientId;
}
