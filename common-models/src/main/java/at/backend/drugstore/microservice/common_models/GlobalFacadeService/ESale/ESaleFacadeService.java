package at.backend.drugstore.microservice.common_models.GlobalFacadeService.ESale;

import at.backend.drugstore.microservice.common_models.DTOs.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface ESaleFacadeService {

    CompletableFuture<Result<Void>> initSale(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO);
    CompletableFuture<Long> makeDigitalSaleAndGetID(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO);
}
