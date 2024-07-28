package microservice.sale_service.Model;

import at.backend.drugstore.microservice.common_models.Models.Sales.SaleItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(name = "physical_sale_items")
public class PhysicalSaleItem extends SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "physical_sale_id", nullable = false)
    private PhysicalSale physicalSale;


}
