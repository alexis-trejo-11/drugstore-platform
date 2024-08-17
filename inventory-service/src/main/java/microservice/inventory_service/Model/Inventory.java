package microservice.inventory_service.Model;

import at.backend.drugstore.microservice.common_classes.DTOs.Inventory.InventoryInsertDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@Table(name = "inventory")
public class  Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_number")
    private String batchNumber;

    private int quantity;

    @Column(name = "expiration_date")
    private Date expirationDate;

    private String location;

    @Column(name = "date_received")
    private Date dateReceived;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column
    private Long productId;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    private List<InventoryTransaction> transactions;


    public Inventory(InventoryInsertDTO inventoryInsertDTO) {
        this.batchNumber = inventoryInsertDTO.getBatchNumber();
        this.dateReceived = inventoryInsertDTO.getDateReceived();
        this.location =  inventoryInsertDTO.getLocation();
        this.expirationDate = inventoryInsertDTO.getExpirationDate();
        this.quantity = inventoryInsertDTO.getQuantity();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}
