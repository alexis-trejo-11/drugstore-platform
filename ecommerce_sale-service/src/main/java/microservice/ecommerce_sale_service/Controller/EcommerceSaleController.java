package microservice.ecommerce_sale_service.Controller;

import at.backend.drugstore.microservice.common_models.DTOs.Sale.DigitalSaleDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.SalesSummaryDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_sale_service.Service.DigitalSaleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1/api/digital-sales")
@Slf4j
public class EcommerceSaleController {

    private final DigitalSaleService digitalSaleService;

    public EcommerceSaleController(DigitalSaleService digitalSaleService) {
        this.digitalSaleService = digitalSaleService;
    }



    @PostMapping
    @Operation(summary = "Create a new digital sale", description = "Creates a new digital sale and updates inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sale created successfully",
                    content = @Content(schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public CompletableFuture<ResponseEntity<ResponseWrapper<DigitalSaleDTO>>> createDigitalSale(@Valid @RequestBody DigitalSaleItemInsertDTO digitalSaleItemInsertDTO) {
        return digitalSaleService.createDigitalSale(digitalSaleItemInsertDTO)
                .thenApply(digitalSaleDTO -> ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseWrapper<>(true, digitalSaleDTO, "Sale successfully created.", HttpStatus.CREATED.value())));
    }



    @GetMapping("/{saleId}")
    @Operation(summary = "Get a sale by ID", description = "Retrieves a digital sale by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale found",
                    content = @Content(schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    public CompletableFuture<ResponseEntity<ResponseWrapper<DigitalSaleDTO>>> getSaleById(@PathVariable Long saleId) {
        return digitalSaleService.getSaleById(saleId)
                .thenApply(digitalSaleDTOOptional -> digitalSaleDTOOptional
                        .map(digitalSaleDTO -> ResponseEntity.ok(new ResponseWrapper<>(true, digitalSaleDTO, "Sale successfully fetched.", HttpStatus.OK.value())))
                        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, "Sale with Id " + saleId + " not found.", HttpStatus.NOT_FOUND.value()))));
    }



    @GetMapping("/today")
    @Operation(summary = "Get today's sales", description = "Retrieves all digital sales made today")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sales retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ResponseWrapper.class)))
    })
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<DigitalSaleDTO>>>> getSalesFromToday() {
        return digitalSaleService.getTodaySales().thenApply(todaySales -> {
            return ResponseEntity.ok(new ResponseWrapper<>(true, todaySales, "Sales successfully fetched", HttpStatus.OK.value()));
        });
    }



    @GetMapping("/today/summary")
    @Operation(summary = "Get today's sales summary", description = "Retrieves a summary of all digital sales made today")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sales summary retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ResponseWrapper.class)))
    })
    public CompletableFuture<ResponseEntity<ResponseWrapper<SalesSummaryDTO>>> getSaleSummaryFromToday() {
        return digitalSaleService.getTodaySummarySales().thenApply(summaryDTO -> {
            return ResponseEntity.ok(new ResponseWrapper<>(true, summaryDTO, "Sale summary successfully fetched", HttpStatus.OK.value()));
        });
    }
}
