package at.backend.drugstore.microservice.common_models.DTO.Order;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderInsertDTO {

    @JsonProperty("client")
    @NotNull(message = "Client Data Is Obligatory")
    private ClientDTO clientDTO;

    @JsonProperty("items")
    @NotNull(message = "Items Are Obligatory")
    @NotEmpty(message = "Item List Can't Be Empty")
    private List<OrderItemInsertDTO> items;

    @JsonProperty("address")
    @NotNull(message = "Address Data Is Obligatory")
    private AddressDTO addressDTO;
}
