package microservice.inventory_service.Model;


import at.backend.drugstore.microservice.common_models.DTO.Inventory.InventoryTransactionInsertDTO;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Table(name = "inventory_transaction")
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

    // Constructor
    public InventoryTransaction() {
    }

    public InventoryTransaction(InventoryTransactionInsertDTO inventoryTransactionInsertDTO, Long employeeId) {
        this.transactionType = inventoryTransactionInsertDTO.getTransactionType();
        this.quantity = inventoryTransactionInsertDTO.getQuantity();
        this.date = inventoryTransactionInsertDTO.getDate();
        this.employeeId = inventoryTransactionInsertDTO.getEmployeeId();
        this.createdAt = LocalDateTime.now();
        this.employeeId = employeeId;

    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}