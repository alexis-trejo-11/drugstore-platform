package at.backend.drugstore.microservice.common_classes.DTOs.Order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaymentStatus {
    @JsonProperty("is_order_paid")
    @NotNull(message = "is_order_paid is obligatory")
    private boolean isOrderPaid;

    @JsonProperty("order_id")
    @NotNull(message = "order_id is obligatory")
    @Positive(message = "order_id must be positive")
    private Long orderId;
}
