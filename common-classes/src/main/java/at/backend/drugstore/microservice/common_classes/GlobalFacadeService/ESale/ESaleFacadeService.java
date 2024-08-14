package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.ESale;

import at.backend.drugstore.microservice.common_classes.DTOs.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface ESaleFacadeService {

    CompletableFuture<Result<Void>> initSale(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO);
    CompletableFuture<Long> makeDigitalSaleAndGetID(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO);
}
