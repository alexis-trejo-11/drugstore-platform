package microservice.client_service.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;

    @Column(name = "house_number")
    private int houseNumber;

    private String neighborhood;

    private String city;

    private String state;

    private String country;

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


    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public enum AddressType {
        HOUSE,
        DEPARTMENT
    }
}