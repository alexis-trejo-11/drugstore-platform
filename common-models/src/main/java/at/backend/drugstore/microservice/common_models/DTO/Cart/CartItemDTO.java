package at.backend.drugstore.microservice.common_models.DTO.Cart;

import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_models.Models.Sales.SaleItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CartItemDTO extends SaleItemDTO {
}