package at.backend.drugstore.microservice.common_classes.DTOs.Order;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "DTOs representing an order.")
public class OrderDTO {

    @Schema(description = "Unique identifier for the order.", example = "12345")
    @JsonProperty("id")
    private Long id;

    @Schema(description = "Unique identifier for the client who placed the order.", example = "67890")
    @JsonProperty("client_id")
    private Long clientId;

    @Schema(description = "Unique identifier for the address associated with the order.", example = "11223")
    @JsonProperty("address_id")
    private Long addressId;

    @Schema(description = "Date and time when the order was placed.", example = "2024-08-05T15:30:00")
    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    @Schema(description = "Current status of the order.", example = "PENDING")
    @JsonProperty("status")
    private OrderStatus status;

    @Schema(description = "List of items included in the order.")
    @JsonProperty("items")
    private List<OrderItemDTO> items;

    @Schema(description = "Shipping address for the order.", example = "123 Elm Street, Springfield")
    @JsonProperty("shipping_address")
    private String shippingAddress;
}
