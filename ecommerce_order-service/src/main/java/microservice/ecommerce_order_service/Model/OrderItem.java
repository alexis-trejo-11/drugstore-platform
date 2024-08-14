package microservice.ecommerce_order_service.Model;

import at.backend.drugstore.microservice.common_classes.Models.Sales.SaleItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "order_items")
public class OrderItem extends SaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

}
