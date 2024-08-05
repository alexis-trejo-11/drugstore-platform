package microservice.ecommerce_sale_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Sale.DigitalSaleDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SalesSummaryDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_sale_service.Service.DigitalSaleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1/api/digital-sales")
@Validated
@Slf4j
public class EcommerceSaleController {

    private final DigitalSaleService digitalSaleService;

    public EcommerceSaleController(DigitalSaleService digitalSaleService) {
        this.digitalSaleService = digitalSaleService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<ApiResponse<DigitalSaleDTO>>> createDigitalSale(@Valid @RequestBody DigitalSaleItemInsertDTO digitalSaleItemInsertDTO) {
        return digitalSaleService.createDigitalSale(digitalSaleItemInsertDTO).thenCompose(digitalSaleDTO -> {
            return digitalSaleService.updateInventory(digitalSaleDTO).thenApply(voidResult -> {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponse<>(true, digitalSaleDTO, "Sale successfully created.", HttpStatus.CREATED.value()));
            });
        });
    }

    @GetMapping("/{saleId}")
    public CompletableFuture<ResponseEntity<ApiResponse<DigitalSaleDTO>>> getSaleById(@PathVariable Long saleId) {
        return digitalSaleService.getSaleById(saleId)
                .thenApply(digitalSaleDTOOptional -> digitalSaleDTOOptional
                        .map(digitalSaleDTO -> ResponseEntity.ok(new ApiResponse<>(true, digitalSaleDTO, "Sale successfully fetched.", HttpStatus.OK.value())))
                        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(false, null, "Sale with Id " + saleId + " not found.", HttpStatus.NOT_FOUND.value()))));
    }

    @GetMapping("/today")
    public CompletableFuture<ResponseEntity<ApiResponse<List<DigitalSaleDTO>>>> getSalesFromToday() {
        return digitalSaleService.getTodaySales().thenApply(todaySales -> {
            return ResponseEntity.ok(new ApiResponse<>(true, todaySales, "Sales successfully fetched", HttpStatus.OK.value()));
        });
    }

    @GetMapping("/today/summary")
    public CompletableFuture<ResponseEntity<ApiResponse<SalesSummaryDTO>>> getSaleSummaryFromToday() {
        return digitalSaleService.getTodaySummarySales().thenApply(summaryDTO -> {
            return ResponseEntity.ok(new ApiResponse<>(true, summaryDTO, "Sale summary successfully fetched", HttpStatus.OK.value()));
        });
    }
}
