package microservice.adress_service.Model;

import at.backend.drugstore.microservice.common_classes.Models.Address.AddressType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Table(name = "clients_addresses")
public class ClientAddress extends  Address {
    private String description;

    @Column(name = "zip_code")
    private int zipCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "inner_number")
    private String innerNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type")
    private AddressType addressType;

    @Column(name = "client_id")
    private long clientId;


}
