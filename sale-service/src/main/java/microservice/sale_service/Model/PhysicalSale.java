package microservice.sale_service.Model;

import at.backend.drugstore.microservice.common_classes.Models.Sales.Sale;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
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

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @OneToMany(mappedBy = "physicalSale", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhysicalSaleItem> saleItems;

    @Column(name = "pay_type")
    private PayType payType;

    public enum PayType {
        CASH, CARD, SHOP_CREDIT

    }
}