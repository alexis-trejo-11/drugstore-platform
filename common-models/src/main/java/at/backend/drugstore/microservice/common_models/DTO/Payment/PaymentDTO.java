package at.backend.drugstore.microservice.common_models.DTO.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PaymentDTO {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("payment_method_id")
    private Long paymentMethodId;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("payment_date")
    private LocalDateTime paymentDate;

    @JsonProperty("card_id")
    private Long cardId;

    @JsonProperty("status")
    private String status;
}
