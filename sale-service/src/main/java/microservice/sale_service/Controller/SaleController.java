package microservice.sale_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Sale.*;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.sale_service.Service.SaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/sales")
public class SaleController {

    private static final Logger logger = LoggerFactory.getLogger(SaleController.class);

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateSaleDTO>> createSale(@RequestBody SaleProductsDTO saleProductsDTO) {
        logger.info("Request to create sale with products: {}", saleProductsDTO);
        Result<CreateSaleDTO> result = saleService.createSale(saleProductsDTO);
        if (result.isSuccess()) {
            return new ResponseEntity<>(new ApiResponse<>(true, result.getData(), "Sale created successfully", HttpStatus.CREATED.value()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(false, null, result.getErrorMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<ProcessSaleDTO>> paySale(@RequestBody PaySaleDTO paySaleDTO) {
        logger.info("Request to pay for sale with ID: {}", paySaleDTO.getSaleId());
        Result<ProcessSaleDTO> result = saleService.paySale(paySaleDTO);
        if (result.isSuccess()) {
            return new ResponseEntity<>(new ApiResponse<>(true, result.getData(), "Sale paid successfully", HttpStatus.OK.value()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(false, null, result.getErrorMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{saleId}")
    public ResponseEntity<ApiResponse<SaleDTO>> getSaleById(@PathVariable Long saleId) {
        logger.info("Request to get sale details with ID: {}", saleId);
        Result<SaleDTO> result = saleService.getSaleById(saleId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(new ApiResponse<>(true, result.getData(), "Sale retrieved successfully", HttpStatus.OK.value()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>(false, null, result.getErrorMessage(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<SaleDTO>>> getTodaySales() {
        logger.info("Request to get today's sales.");
        List<SaleDTO> sales = saleService.getTodaySales();
        return new ResponseEntity<>(new ApiResponse<>(true, sales, "Today's sales retrieved successfully", HttpStatus.OK.value()), HttpStatus.OK);
    }

    @GetMapping("/today/summary")
    public ResponseEntity<ApiResponse<SalesSummaryDTO>> getTodaySummarySales() {
        logger.info("Request to get summary of today's sales.");
        SalesSummaryDTO summary = saleService.getTodaySummarySales();
        return new ResponseEntity<>(new ApiResponse<>(true, summary, "Summary of today's sales retrieved successfully", HttpStatus.OK.value()), HttpStatus.OK);
    }
}
