package at.backend.drugstore.microservice.common_models.DTO.Payment;

import at.backend.drugstore.microservice.common_models.Utils.MonthYearDeserializer;
import at.backend.drugstore.microservice.common_models.Utils.MonthYearSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CardInsertDTO {

    @JsonProperty("client_id")
    @NotNull(message = "client_id is obligatory")
    private Long clientId;

    @JsonProperty("card_number")
    @NotNull(message = "card_number is obligatory")
    @NotBlank(message = "card_number is empty")
    @Pattern(regexp = "\\b\\d{16}\\b", message = "card_number must be 16 digits")
    private String cardNumber;

    @JsonProperty("cardholder_name")
    @NotNull(message = "cardholder_name is obligatory")
    @NotBlank(message = "cardholder_name is empty")
    private String cardholderName;

    @JsonProperty("expiration_date")
    @NotNull(message = "expiration_date is obligatory")
    @JsonSerialize(using = MonthYearSerializer.class)
    @JsonDeserialize(using = MonthYearDeserializer.class)
    private LocalDate expirationDate;

    @JsonProperty("cvv")
    @NotNull(message = "cvv is obligatory")
    @NotBlank(message = "cvv is empty")
    @Pattern(regexp = "\\b\\d{3}\\b", message = "cvv must be 3 digits")
    private String cvv;

    @JsonProperty("card_type")
    @NotNull(message = "card_type is obligatory")
    @NotBlank(message = "card_type is empty")
    private String cardType;

    public boolean isValidCardType() {
        return cardType != null && (cardType.equalsIgnoreCase("VISA") || cardType.equalsIgnoreCase("MASTERCARD") || cardType.equalsIgnoreCase("AMEX") || cardType.equalsIgnoreCase("DISCOVER"));
    }
}
