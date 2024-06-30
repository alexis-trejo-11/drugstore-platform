package microservice.ecommerce_sale_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SalesSummaryDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_sale_service.Service.DigitalSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/sales")
public class EcommerceSaleController {

    private static final Logger logger = Logger.getLogger(EcommerceSaleController.class.getName());

    private final DigitalSaleService digitalSaleService;

    @Autowired
    public EcommerceSaleController(DigitalSaleService digitalSaleService) {
        this.digitalSaleService = digitalSaleService;
    }

    @PostMapping("/create/{saleId}/{orderId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> makeDigitalSale(@RequestBody CartDTO cartDTO, SaleDTO saleDTO) {
        return digitalSaleService.createDigitalSale(saleDTO, cartDTO)
                .thenApplyAsync(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage(), HttpStatus.NOT_FOUND);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else {
                        ResponseWrapper<Void> response = new ResponseWrapper<>(null, null, HttpStatus.OK);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                })
                .exceptionally(ex -> {
                    logger.log(Level.SEVERE, "Error processing sale creation", ex);
                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    @PostMapping("/create/{saleId}/{orderId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> updateSaleOrder(@PathVariable Long saleId, Long orderId) {
        return digitalSaleService.addOrderToSale(saleId, orderId)
                .thenApplyAsync(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage(), HttpStatus.NOT_FOUND);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else {
                        ResponseWrapper<Void> response = new ResponseWrapper<>(null, null, HttpStatus.OK);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                })
                .exceptionally(ex -> {
                    logger.log(Level.SEVERE, "Error processing sale update", ex);
                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }



    @GetMapping("/{saleId}")
    public CompletableFuture<ResponseEntity<?>> getSaleById(@PathVariable Long saleId) {
        return CompletableFuture.supplyAsync(() -> {
            Result<?> result = digitalSaleService.getSaleById(saleId);
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
           var todaySales = digitalSaleService.getTodaySales();
           return ResponseEntity.status(HttpStatus.OK).body(todaySales);
        }).exceptionally(ex -> {
            logger.log(Level.SEVERE, "Error Finding Sales", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        });
    }


    @GetMapping("/today/summary")
    public CompletableFuture<ResponseEntity<SalesSummaryDTO>> getSaleSummaryFromToday() {
        return digitalSaleService.getTodaySummarySales()
                .thenApply(summary -> ResponseEntity.status(HttpStatus.OK).body(summary))
                .exceptionally(ex -> {
                    logger.log(Level.SEVERE, "Error Getting Summary", ex);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

}
