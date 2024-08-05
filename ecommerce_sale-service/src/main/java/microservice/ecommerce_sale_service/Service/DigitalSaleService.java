package microservice.ecommerce_sale_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Sale.DigitalSaleDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SalesSummaryDTO;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface DigitalSaleService {
    CompletableFuture<DigitalSaleDTO> createDigitalSale(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO);
    CompletableFuture<Void> updateInventory(DigitalSaleDTO digitalSaleDTO);
    CompletableFuture<Optional<DigitalSaleDTO>> getSaleById(Long saleId);
    CompletableFuture<List<DigitalSaleDTO>> getTodaySales();
    CompletableFuture<SalesSummaryDTO> getTodaySummarySales();
}
