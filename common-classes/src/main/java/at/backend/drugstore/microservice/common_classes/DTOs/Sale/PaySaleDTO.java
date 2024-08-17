package at.backend.drugstore.microservice.common_classes.DTOs.Sale;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PaySaleDTO {

    @NotNull(message = "Sale Id Is Obligatory")
    @Positive(message = "Sale Id Must Be Positive")
    @JsonProperty("sale_id")
    private  Long saleId;

    @JsonProperty("coupon")
    private String coupon;

    @JsonProperty("money_provided")
    @NotNull(message = "Money Is Obligatory")
    @PositiveOrZero(message = "Money Provided Can't Be Negative")
    private BigDecimal moneyProvided;

    @JsonProperty("pay_type")
    private String payType;


}
