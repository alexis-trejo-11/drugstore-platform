package at.backend.drugstore.microservice.common_models.DTO.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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

    @JsonProperty("amount")
    @NotNull(message = "amount is obligatory.")
    @Positive(message = "amount must be positive.")
    private BigDecimal amount;

    @JsonProperty("card_id")
    private Long cardId;

    @JsonProperty("order_id")
    private Long orderId;

}
