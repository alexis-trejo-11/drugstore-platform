package microservice.ecommerce_payment_service.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("client_id")
    private Long clientId;

    @Enumerated(EnumType.STRING)
    @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;

    private BigDecimal subtotal;

    private BigDecimal discount;

    private BigDecimal total;

    @JsonProperty("payment_date")
    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "order_id")
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PaymentStatus {
        SUCCESS, FAILURE, PENDING
    }

    public enum PaymentMethod {
        CARD, CASH
    }
}
