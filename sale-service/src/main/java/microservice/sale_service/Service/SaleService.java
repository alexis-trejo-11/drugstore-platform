package microservice.sale_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Sale.*;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SaleService {
    CreateSaleDTO createSale(SaleInsertDTO saleInsertDTO, Long employeeId);
    Result<SaleDTO> getSaleById(Long saleId);
    List<SaleDTO> getTodaySales();
    SalesSummaryDTO getTodaySummarySales();

    }
