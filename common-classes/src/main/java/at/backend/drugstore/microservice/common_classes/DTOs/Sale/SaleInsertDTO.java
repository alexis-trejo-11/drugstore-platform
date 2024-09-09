package at.backend.drugstore.microservice.common_classes.DTOs.Sale;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class SaleInsertDTO {

    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("subtotal")
    @NotNull(message = "subtotal Is Obligatory")
    @Positive(message = "subtotal Can't Be Negative")
    private BigDecimal subtotal;

    @JsonProperty("discount")
    @NotNull(message = "discount Is Obligatory")
    @Positive(message = "discount Can't Be Negative")
    private BigDecimal discount;

    @JsonProperty("total")
    @NotNull(message = "total Is Obligatory")
    @Positive(message = "total Can't Be Negative")
    private BigDecimal total;

    @JsonProperty("money_provided")
    @NotNull(message = "Money Is Obligatory")
    @PositiveOrZero(message = "Money Provided Can't Be Negative")
    private BigDecimal moneyProvided;

    @JsonProperty("items")
    @NotNull(message = "items Is Obligatory")
    private List<SaleItemInsertDTO> items;

    // CASH, CARD, SHOP_CREDIT
    @JsonProperty("pay_type")
    @NotNull(message = "pay_type Is Obligatory")
    @NotEmpty(message = "pay_type Can't Be Negative")
    private String payType;

    public List<Long> getProductsId() {
        List<Long> productIds = new ArrayList<>();
        for (var item : items) {
            productIds.add(item.getProductId());
        }
        return productIds;
    }
}

