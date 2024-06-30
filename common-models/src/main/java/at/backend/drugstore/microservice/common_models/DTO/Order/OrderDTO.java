package at.backend.drugstore.microservice.common_models.DTO.Order;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("client_name")
    private String clientName;

    @JsonProperty("client_phone")
    private String clientPhone;

    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    @JsonProperty("status")
    private OrderStatus status;

    @JsonProperty("items")
    private List<OrderItemDTO> items;

    @JsonProperty("address")
    private AddressDTO address;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("payment_method")
    private String paymentMethod;

}
