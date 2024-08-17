package at.backend.drugstore.microservice.common_classes.DTOs.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PaymentInsertDTO {

    @JsonProperty("client_id")
    @NotNull(message = "client_id is obligatory.")
    @Positive(message = "client_id must be positive.")
    private Long clientId;

    @JsonProperty("payment_method")
    @NotNull(message = "payment_method is obligatory.")
    @NotBlank(message = "payment_method must has content.")
    private String paymentMethod;

    @JsonProperty("subtotal")
    @NotNull(message = "subtotal is obligatory.")
    @Positive(message = "subtotal must be positive.")
    private BigDecimal subtotal;

    @JsonProperty("discount")
    @NotNull(message = "discount is obligatory.")
    @PositiveOrZero(message = "discount can not be a negative number.")
    private BigDecimal discount;

    @JsonProperty("total")
    @NotNull(message = "total is obligatory.")
    @Positive(message = "total must be positive.")
    private BigDecimal total;

    @JsonProperty("card_id")
    private Long cardId;

    @JsonProperty("order_id")
    private Long orderId;

}
