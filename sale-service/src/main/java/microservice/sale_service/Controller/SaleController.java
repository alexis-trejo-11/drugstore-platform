package microservice.sale_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Sale.PaySaleDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleProductsDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SalesSummaryDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.sale_service.Service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/sales")
public class SaleController {

    private static final Logger logger = Logger.getLogger(SaleController.class.getName());

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<?>> initSale(@RequestBody SaleProductsDTO saleProductsDTO) {
        return CompletableFuture.supplyAsync(() -> {
            Result<?> result = saleService.createSale(saleProductsDTO);
            if (!result.isSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getErrorMessage());
            } else {
                return ResponseEntity.status(HttpStatus.CREATED).body(result.getData());
            }
        }).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Error processing sale creation", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        });
    }

    @PutMapping("/process")
    public CompletableFuture<ResponseEntity<?>> processSale(@Valid @RequestBody PaySaleDTO paySaleDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            ResponseWrapper<String> responseWrapper = new ResponseWrapper<>(null, errors.toString());
            return CompletableFuture.completedFuture(new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST));
        }

        return CompletableFuture.supplyAsync(() -> {
            Result<?> result = saleService.paySale(paySaleDTO);
            if (!result.isSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getErrorMessage());
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(result.getData());
            }
        }).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Error processing sale payment", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        });
    }

    @GetMapping("/{saleId}")
    public CompletableFuture<ResponseEntity<?>> getSaleById(@PathVariable Long saleId) {
        return CompletableFuture.supplyAsync(() -> {
            Result<?> result = saleService.getSaleById(saleId);
            if (!result.isSuccess()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.getErrorMessage());
            }
            return ResponseEntity.status(HttpStatus.OK).body(result.getData());
        }).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Error retrieving sale by ID", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        });
    }


    @GetMapping("/today")
    public CompletableFuture<ResponseEntity<List<SaleDTO>>> getSalesFromToday() {
        return CompletableFuture.supplyAsync( () -> {
           var todaySales = saleService.getTodaySales();
           return ResponseEntity.status(HttpStatus.OK).body(todaySales);
        }).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Error Finding Sales", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        });
    }


    @GetMapping("/today/summary")
    public CompletableFuture<ResponseEntity<SalesSummaryDTO>> getSaleSummaryFromToday() {
        return saleService.getTodaySummarySales()
                .thenApply(summary -> ResponseEntity.status(HttpStatus.OK).body(summary))
                .exceptionally(ex -> {
                    logger.log(Level.SEVERE, "Error Getting Summary", ex);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

}
