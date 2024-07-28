package microservice.sale_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Sale.*;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.sale_service.Model.PhysicalSale;

import java.util.List;

public interface SaleService {
    Result<CreateSaleDTO> createSale(SaleProductsDTO saleProductsDTO);
    Result<ProcessSaleDTO> paySale(PaySaleDTO paySaleDTO);
    Result<SaleDTO> getSaleById(Long saleId);
    List<SaleDTO> getTodaySales();
    SalesSummaryDTO getTodaySummarySales();

    }
