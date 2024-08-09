package microservice.ecommerce_sale_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Sale.DigitalSaleDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.SalesSummaryDTO;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface DigitalSaleService {
    CompletableFuture<DigitalSaleDTO> createDigitalSale(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO);
    CompletableFuture<Optional<DigitalSaleDTO>> getSaleById(Long saleId);
    CompletableFuture<List<DigitalSaleDTO>> getTodaySales();
    CompletableFuture<SalesSummaryDTO> getTodaySummarySales();
}
