package microservice.ecommerce_order_service.Model;


import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "last_order_update")
    private LocalDateTime lastOrderUpdate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private int deliveryTries;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_data_id", referencedColumnName = "id")
    private ShippingData shippingData;
}
