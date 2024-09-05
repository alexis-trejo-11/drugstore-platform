package microservice.client_service.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("first_name")
    @Column(name = "first_name")
    private String firstName;

    @JsonProperty("last_name")
    @Column(name = "last_name")
    private String lastName;

    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "birth_date")
    private LocalDate birthdate;

    @JsonProperty("phone")
    @Column(name = "phone")
    private String phone;

    @JsonProperty("is_active")
    @Column(name = "is_active")
    private boolean isActive;

    @JsonProperty("is_client_premium")
    @Column(name = "is_client_premium")
    private boolean isClientPremium;

    @JsonProperty("loyalty_points")
    @Column(name = "loyalty_points")
    private int loyaltyPoints;

    @JsonProperty("joined_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @JsonProperty("last_action")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "last_action")
    private LocalDateTime lastAction;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Address> addresses;
}
