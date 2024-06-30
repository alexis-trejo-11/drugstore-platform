package at.backend.drugstore.microservice.common_models.DTO.Sale;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProcessSaleDTO  {

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("amount_paid")
    private BigDecimal amountPaid;

    @JsonProperty("change")
    private BigDecimal change;

    private BigDecimal subTotal;

    private BigDecimal discount;

    private BigDecimal total;
}
