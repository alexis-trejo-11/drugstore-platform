package microservice.ecommerce_order_service.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "shipping_data")
public class ShippingData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    private String country;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "shipping_cost")
    private BigDecimal shippingCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_status")
    private ShippingStatus shippingStatus;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @OneToOne(mappedBy = "shippingData", cascade = CascadeType.ALL)
    private Order order;
}
