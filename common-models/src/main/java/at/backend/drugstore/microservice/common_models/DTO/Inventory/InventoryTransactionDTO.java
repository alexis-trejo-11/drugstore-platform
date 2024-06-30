package at.backend.drugstore.microservice.common_models.DTO.Inventory;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class InventoryTransactionDTO {

    private Long id;

    private String transactionType;

    private int quantity;

    private Date date;

    private Long employeeId;

}
