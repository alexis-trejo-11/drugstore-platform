package microservice.sale_service.Model;

import at.backend.drugstore.microservice.common_models.Models.Sales.Sale;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "physical_sales")
public class PhysicalSale extends Sale {

    public PhysicalSale() {
        super();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    private String employeeName;

    @OneToMany(mappedBy = "physicalSale", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhysicalSaleItem> saleItems;

    private PayType payType;

    public enum PayType {
        CASH, CARD, SHOP_CREDIT

    }
}