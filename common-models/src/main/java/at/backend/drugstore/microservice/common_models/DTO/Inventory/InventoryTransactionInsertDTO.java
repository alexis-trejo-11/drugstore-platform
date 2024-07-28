package at.backend.drugstore.microservice.common_models.DTO.Inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
public class InventoryTransactionInsertDTO {

    @NotBlank
    @JsonProperty("transaction_type")
    private String transactionType;

    @Min(value = 1, message = "Quantity must be greater than or equal to 1.")
    private int quantity;

    @NotNull(message = "Date It's Obligatory")
    private Date date;

    @JsonProperty("employee_id")
    private Long employeeId;

}
