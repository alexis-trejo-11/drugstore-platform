package microservice.ecommerce_sale_service.Model;

import at.backend.drugstore.microservice.common_classes.Models.Sales.SaleItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(name = "digital_sale_items")
public class DigitalSaleItem extends SaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "digital_sale_id", nullable = false)
    private DigitalSale digitalSale;


}
