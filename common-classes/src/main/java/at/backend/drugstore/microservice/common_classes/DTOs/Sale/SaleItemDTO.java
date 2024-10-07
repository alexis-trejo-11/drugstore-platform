package at.backend.drugstore.microservice.common_classes.DTOs.Sale;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Schema(description = "Data Transfer Object for a Sale Item")
public class SaleItemDTO {

    @Schema(description = "Unique identifier of the product")
    @JsonProperty("product_id")
    private Long productId;

    @Schema(description = "Name of the product")
    @JsonProperty("product_name")
    private String productName;

    @Schema(description = "Quantity of the product in this sale item")
    @JsonProperty("product_quantity")
    private int productQuantity;

    @Schema(description = "Unit price of the product")
    @JsonProperty("product_unit_price")
    private BigDecimal productUnitPrice;

    @Schema(description = "Total price for this sale item (productQuantity * unit price)")
    @JsonProperty("item_total")
    private BigDecimal itemTotal;

    public void setCalculateItemTotal() {
        this.itemTotal = this.productUnitPrice.multiply(BigDecimal.valueOf(this.productQuantity));
    }
}