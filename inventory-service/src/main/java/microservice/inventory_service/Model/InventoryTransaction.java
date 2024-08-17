package microservice.inventory_service.Model;


import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryTransactionInsertDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Table(name = "inventory_transaction")
@Data
@NoArgsConstructor
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_type")
    private String transactionType;

    private int quantity;

    private Date date;

    private LocalDateTime createdAt;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @Column(name = "employee_id")
    private Long employeeId;

    public InventoryTransaction(InventoryTransactionInsertDTO inventoryTransactionInsertDTO, Long employeeId) {
        this.transactionType = inventoryTransactionInsertDTO.getTransactionType();
        this.quantity = inventoryTransactionInsertDTO.getQuantity();
        this.date = inventoryTransactionInsertDTO.getDate();
        this.employeeId = inventoryTransactionInsertDTO.getEmployeeId();
        this.createdAt = LocalDateTime.now();
        this.employeeId = employeeId;

    }
}