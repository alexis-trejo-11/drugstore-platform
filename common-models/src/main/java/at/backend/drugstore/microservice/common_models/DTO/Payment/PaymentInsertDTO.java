package at.backend.drugstore.microservice.common_models.DTO.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PaymentInsertDTO {

    @JsonProperty("user_id")
    @NotNull
    @Positive
    private Long userId;

    @JsonProperty("payment_method_id")
    @NotNull
    @Positive
    private Long paymentMethodId;

    @JsonProperty("amount")
    @NotNull
    @Positive
    private BigDecimal amount;

    @JsonProperty("card_id")
    private Long cardId;
}
