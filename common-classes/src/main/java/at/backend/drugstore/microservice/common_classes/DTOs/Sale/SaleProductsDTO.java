package at.backend.drugstore.microservice.common_classes.DTOs.Sale;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class SaleProductsDTO {

    private Long clientId;

    @JsonProperty("cashier_id")
    private Long cashierId;

    @JsonProperty("items")
    private List<SaleItemInsertDTO> items;

    public List<Long> getProductsId() {

        List<Long> productIds = new ArrayList<>();
        for (var item : items) {
            productIds.add(item.getProductId());
        }
        return productIds;
    }



}
