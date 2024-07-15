package microservice.ecommerce_sale_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Sale.DigitalSaleDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SalesSummaryDTO;
import java.util.List;
import java.util.Optional;

public interface DigitalSaleService {
    DigitalSaleDTO createDigitalSale(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO);
    void updateInventory(DigitalSaleDTO digitalSaleDTO);
    Optional<DigitalSaleDTO> getSaleById(Long saleId);
    List<DigitalSaleDTO> getTodaySales();
    SalesSummaryDTO getTodaySummarySales();
}
