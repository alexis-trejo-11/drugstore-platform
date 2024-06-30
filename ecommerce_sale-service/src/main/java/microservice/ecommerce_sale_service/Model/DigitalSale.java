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

    @OneToMany(mappedBy = "physicalSale", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<DigitalSaleItem> saleItems;

    private DigitalPayType payType;

    private Long ClientPaymentId;

    private Long orderId;

    public enum DigitalPayType {
        CREDIT, CARD, SHOP_CREDIT

    }
}