package at.backend.drugstore.microservice.common_models.DTO.Order;


import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleItemDTO;
import at.backend.drugstore.microservice.common_models.Models.Sales.SaleItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class OrderItemDTO extends SaleItemDTO {
    @JsonProperty("order_id")
    private Long orderId;

}
