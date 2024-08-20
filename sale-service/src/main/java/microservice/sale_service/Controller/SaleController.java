package microservice.sale_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Sale.*;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import microservice.sale_service.Service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("v1/api/sales")
@Tag(name = "Drugstore Microservice API (Sale Service)", description = "Service for managing phisical sales")
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @Operation(summary = "Create a new sale")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sale created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<ResponseWrapper<CreateSaleDTO>>> createSale(@RequestBody SaleProductsDTO saleProductsDTO) {
        log.info("Request to create sale with products: {}", saleProductsDTO);
        return saleService.createSale(saleProductsDTO)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        return new ResponseEntity<>(new ResponseWrapper<>(true, result.getData(), "Sale created successfully", HttpStatus.CREATED.value()), HttpStatus.CREATED);
                    }
                    return new ResponseEntity<>(new ResponseWrapper<>(false, null, result.getErrorMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
                });
    }

    @Operation(summary = "Pay for an existing sale")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale paid successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/pay")
    public CompletableFuture<ResponseEntity<ResponseWrapper<ProcessSaleDTO>>> paySale(@RequestBody PaySaleDTO paySaleDTO) {
        log.info("Request to pay for sale with ID: {}", paySaleDTO.getSaleId());
        return saleService.paySale(paySaleDTO)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        return new ResponseEntity<>(new ResponseWrapper<>(true, result.getData(), "Sale paid successfully", HttpStatus.OK.value()), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(new ResponseWrapper<>(false, null, result.getErrorMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
                    }
                });
    }

    @Operation(summary = "Get sale details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @GetMapping("/{saleId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<SaleDTO>>> getSaleById(@PathVariable Long saleId) {
        log.info("Request to get sale details with ID: {}", saleId);
        return saleService.getSaleById(saleId)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        return new ResponseEntity<>(new ResponseWrapper<>(true, result.getData(), "Sale retrieved successfully", HttpStatus.OK.value()), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(new ResponseWrapper<>(false, null, result.getErrorMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
                    }
                });
    }

    @Operation(summary = "Get today's sales")
    @ApiResponse(responseCode = "200", description = "Today's sales retrieved successfully")
    @GetMapping("/today")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<SaleDTO>>>> getTodaySales() {
        log.info("Request to get today's sales.");
        return saleService.getTodaySales()
                .thenApply(sales -> new ResponseEntity<>(new ResponseWrapper<>(true, sales, "Today's sales retrieved successfully", HttpStatus.OK.value()), HttpStatus.OK));
    }

    @Operation(summary = "Get summary of today's sales")
    @ApiResponse(responseCode = "200", description = "Summary of today's sales retrieved successfully")
    @GetMapping("/today/summary")
    public CompletableFuture<ResponseEntity<ResponseWrapper<SalesSummaryDTO>>> getTodaySummarySales() {
        log.info("Request to get summary of today's sales.");
        return saleService.getTodaySummarySales()
                .thenApply(summary -> new ResponseEntity<>(new ResponseWrapper<>(true, summary, "Summary of today's sales retrieved successfully", HttpStatus.OK.value()), HttpStatus.OK));
    }
}
