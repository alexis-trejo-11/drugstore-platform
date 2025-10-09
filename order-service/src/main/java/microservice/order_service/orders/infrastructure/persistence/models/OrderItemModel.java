package microservice.order_service.orders.infrastructure.persistence.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderModel order;

    @Column(name = "product_id", nullable = false, length = 36)
    private String productID;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "is_prescription_required")
    private Boolean isPrescriptionRequired;

    public boolean isPrescriptionRequired() { return isPrescriptionRequired != null && isPrescriptionRequired; }

}
