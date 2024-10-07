package microservice.ecommerce_sale_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Sale.DigitalSaleDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.SalesSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface DigitalSaleService {
    DigitalSaleDTO createDigitalSale(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO);

    DigitalSaleDTO getSaleById(Long saleId);
    Page<DigitalSaleDTO> getTodaySales(Pageable pageable);
    SalesSummaryDTO getTodaySummarySales(Pageable pageable);
    boolean validateExistingSale(Long saleId);
}
