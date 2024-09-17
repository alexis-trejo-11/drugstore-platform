package microservice.inventory_service.Utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class TransactionOperation {
    @JsonProperty("product_id")
    private Long product_id;

    @JsonProperty("quantity")
    private int quantity;
}
