package microservice.ecommerce_payment_service.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.ecommerce_payment_service.Utils.CreditCardConverter;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id")
    private Long clientId;

    @Convert(converter = CreditCardConverter.class)
    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "cardholder_name")
    private String cardholderName;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Convert(converter = CreditCardConverter.class)
    @Column(name = "cvv")
    private String cvv;

    @Column(name = "is_card_valid")
    private boolean isCardValid;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private CardType cardType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum CardType {
        VISA, MASTERCARD, AMEX, DISCOVER
    }
}
