package at.backend.drugstore.microservice.common_classes.DTOs.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PaymentDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("discount")
    private BigDecimal discount;

    @JsonProperty("subtotal")
    private BigDecimal subtotal;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("payment_date")
    private LocalDateTime paymentDate;

    @JsonProperty("card_id")
    private Long cardId;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("payment_status")
    private String paymentStatus;
}
