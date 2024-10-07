package microservice.client_service.Model;

import at.backend.drugstore.microservice.common_classes.Models.Persons.Person;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
@Table(name = "clients")
public class Client extends Person {
    @JsonProperty("is_client_premium")
    @Column(name = "is_client_premium", nullable = false)
    private boolean isClientPremium;

    @JsonProperty("loyalty_points")
    @Column(name = "loyalty_points" , nullable = false)
    private int loyaltyPoints;

    @JsonProperty("last_action")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "last_action", nullable = false)
    private LocalDateTime lastAction;

    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
    }

    public void deductLoyaltyPoints(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Points to deduct must be positive");
        }

        if (this.loyaltyPoints < points) {
            this.loyaltyPoints = 0;
        } else {
            this.loyaltyPoints -= points;
        }
    }
}
