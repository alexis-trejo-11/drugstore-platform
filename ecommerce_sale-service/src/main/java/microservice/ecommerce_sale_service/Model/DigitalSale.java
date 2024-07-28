package microservice.ecommerce_sale_service.Model;

import at.backend.drugstore.microservice.common_models.Models.Sales.Sale;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "digital_sales")
public class DigitalSale extends Sale {

    public DigitalSale() {
        super();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "digitalSale", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DigitalSaleItem> saleItems;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "payment_id")
    private Long paymentId;

}