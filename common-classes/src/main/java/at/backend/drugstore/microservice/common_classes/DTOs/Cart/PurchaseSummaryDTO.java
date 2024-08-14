package at.backend.drugstore.microservice.common_classes.DTOs.Cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class PurchaseSummaryDTO {

    @JsonProperty("total_purchase_amount")
    private BigDecimal totalPurchaseAmount;

    @JsonProperty("purchased_items")
    private List<CartItemDTO> purchasedItems;

}