package at.backend.drugstore.microservice.common_models.DTO.Cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
public class PurchaseFromCartDTO {

    @JsonProperty("client_id")
    @NotNull(message = "client_id is obligatory")
    @Positive(message = "client_id must be positive")
    private Long clientId;

    @JsonProperty("payment_method")
    @NotNull(message = "payment_method is obligatory")
    @NotBlank(message = "payment_method can not be empty")
    private String paymentMethod;

    @JsonProperty("card_id")
    private Long cardId;
}
