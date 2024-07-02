package microservice.ecommerce_payment_service.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("client_id")
    private Long clientId;

    @Enumerated(EnumType.STRING)
    @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("payment_created_at")
    private LocalDateTime paymentCreatedAt;

    @JsonProperty("payment_date")
    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card; // Nullable for payments made with cash

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    public enum PaymentStatus {
        SUCCESS, FAILURE, PENDING
    }

    public enum PaymentMethod {
        CARD, CASH
    }
}
