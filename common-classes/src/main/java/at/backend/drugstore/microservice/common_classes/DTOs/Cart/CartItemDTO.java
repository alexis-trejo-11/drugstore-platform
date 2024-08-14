package at.backend.drugstore.microservice.common_classes.DTOs.Cart;

import at.backend.drugstore.microservice.common_classes.DTOs.Sale.SaleItemDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CartItemDTO extends SaleItemDTO {
}