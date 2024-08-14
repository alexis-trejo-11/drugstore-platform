package at.backend.drugstore.microservice.common_classes.DTOs.Cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class AddProductsCartDTO {
    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("product_quantity")
    private Map<Long, Integer> productQuantity;

    public List<Long> getProductsIds() {
        return new ArrayList<>(productQuantity.keySet());
    }

}
