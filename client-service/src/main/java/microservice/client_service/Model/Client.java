package microservice.client_service.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthdate;

    @Column(name = "phone")
    private String phone;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_client_premium")
    private boolean isClientPremium;

    @Column(name = "loyalty_points")
    private int loyaltyPoints;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "last_action")
    private LocalDateTime lastAction;

    @OneToMany(mappedBy = "client")
    private List<Address> addresses;


}
