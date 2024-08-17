package at.backend.drugstore.microservice.common_classes.DTOs.Sale;

import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

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
