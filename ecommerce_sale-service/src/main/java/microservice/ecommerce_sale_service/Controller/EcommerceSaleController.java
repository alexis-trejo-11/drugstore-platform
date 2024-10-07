package microservice.ecommerce_sale_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Sale.DigitalSaleDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.SalesSummaryDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_sale_service.Service.DigitalSaleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1/drugstore/ecommerce-sales")
@Tag(name = "Drugstore Microservice API (E-Sale Service)", description = "Service for managing electronic sales")
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
    public ResponseEntity<ResponseWrapper<DigitalSaleDTO>> createDigitalSale(@Valid @RequestBody DigitalSaleItemInsertDTO digitalSaleItemInsertDTO) {
        DigitalSaleDTO digitalSaleDTO = digitalSaleService.createDigitalSale(digitalSaleItemInsertDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created(digitalSaleDTO, "E-Sale"));
    }

    @GetMapping("/{saleId}")
    @Operation(summary = "Get a sale by ID", description = "Retrieves a digital sale by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale found",
                    content = @Content(schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    public ResponseEntity<ResponseWrapper<DigitalSaleDTO>> getSaleById(@PathVariable Long saleId) {
       boolean isSaleExisting = digitalSaleService.validateExistingSale(saleId);
       if (!isSaleExisting) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Sale", "Id"));
       }

       DigitalSaleDTO digitalSaleDTO = digitalSaleService.getSaleById(saleId);
       return ResponseEntity.ok(ResponseWrapper.found(digitalSaleDTO, "E-Sale"));

    }

    @GetMapping("/today")
    @Operation(summary = "Get today's sales", description = "Retrieves all digital sales made today")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sales retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ResponseWrapper.class)))
    })
    public ResponseWrapper<Page<DigitalSaleDTO>> getSalesFromToday(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DigitalSaleDTO> digitalSaleDTOPage = digitalSaleService.getTodaySales(pageable);

        return ResponseWrapper.ok(digitalSaleDTOPage,"Today E-Sale", "Retrieve");
    }

    @GetMapping("/today/summary")
    @Operation(summary = "Get today's sales summary", description = "Retrieves a summary of all digital sales made today")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sales summary retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ResponseWrapper.class)))
    })
    public ResponseWrapper<SalesSummaryDTO> getSaleSummaryFromToday(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        SalesSummaryDTO salesSummaryDTO = digitalSaleService.getTodaySummarySales(pageable);

        return ResponseWrapper.ok(salesSummaryDTO,"E-Sale Summary", "Retrieve");
    }
}
