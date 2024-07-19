package at.backend.drugstore.microservice.common_models.DTO.Order;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInsertDTO {

    @NotNull(message = "client_id is obligatory")
    @Positive(message = "client_id must be postive")
    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("cart")
    @NotNull(message = "cart is obligatory")
    private CartDTO cartDTO;

    @JsonProperty("address_id")
    @NotNull(message = "address_id is obligatory")
    private Long addressId;
}
