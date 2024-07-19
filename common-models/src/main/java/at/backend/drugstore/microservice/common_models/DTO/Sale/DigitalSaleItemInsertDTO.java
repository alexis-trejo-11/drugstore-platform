package at.backend.drugstore.microservice.common_models.DTO.Sale;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderItemDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class DigitalSaleItemInsertDTO {
    @JsonProperty("payment_data")
    @NotNull(message = "payment_data are obligatory")
    PaymentDTO paymentDTO;

    @JsonProperty("items_to_purchase")
    @NotNull(message = "Items to purchase are obligatory")
    @NotEmpty(message = "Items to purchase cannot be empty")
    private List<OrderItemDTO> orderItemDTOS;
}
