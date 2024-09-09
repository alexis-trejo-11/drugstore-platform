package microservice.sale_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Sale.*;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
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
    public ResponseEntity<ResponseWrapper<CreateSaleDTO>> createSale(@RequestBody SaleInsertDTO saleInsertDTO, @RequestParam Long employeeId) {
        log.info("Request to create sale with products: {}", saleInsertDTO);
        CreateSaleDTO createSaleDTO = saleService.createSale(saleInsertDTO, employeeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created(createSaleDTO, "Sale"));
    }

    @Operation(summary = "Get sale details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @GetMapping("/{saleId}")
    public ResponseEntity<ResponseWrapper<SaleDTO>> getSaleById(@PathVariable Long saleId) {
        log.info("Request to get sale details with ID: {}", saleId);

        Result<SaleDTO> saleDTOResult = saleService.getSaleById(saleId);
        if (!saleDTOResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Sale", "ID"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.found(saleDTOResult.getData(), "Sale"));
    }

    @Operation(summary = "Get today's sales")
    @ApiResponse(responseCode = "200", description = "Today's sales retrieved successfully")
    @GetMapping("/today")
    public ResponseWrapper<List<SaleDTO>> getTodaySales() {
        log.info("Request to get today's sales.");
        List<SaleDTO> saleDTOS = saleService.getTodaySales();
        return ResponseWrapper.ok("Today Sales", "Retrieve", saleDTOS);

    }

    @Operation(summary = "Get summary of today's sales")
    @ApiResponse(responseCode = "200", description = "Summary of today's sales retrieved successfully")
    @GetMapping("/today/summary")
    public ResponseWrapper<SalesSummaryDTO> getTodaySummarySales() {
        log.info("Request to get summary of today's sales.");
        SalesSummaryDTO salesSummaryDTO = saleService.getTodaySummarySales();
        return ResponseWrapper.ok("Sale Summary", "Retrieve", salesSummaryDTO);
    }
}
