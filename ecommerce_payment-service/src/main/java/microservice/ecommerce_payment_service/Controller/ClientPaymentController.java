package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_payment_service.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/v1/api/user-payments")
@Tag(name = "Drugstore Microservice API (E-Payment Service)", description = "Service for managing client payments")
public class ClientPaymentController {

    private final PaymentService paymentService;

    public ClientPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Retrieve completed payments by client ID",
            description = "Fetches completed payments for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Completed payments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found or no completed payments found")
    })
    @GetMapping("/client/{clientId}/completed")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<PaymentDTO>>>> getCompletedPaymentsByClientId(@PathVariable Long clientId) {
        log.info("Request to retrieve completed payments for client ID: {}", clientId);

        return paymentService.getCompletedPaymentsByClientId(clientId)
                .thenApply(payments -> {
                    if (payments.isEmpty()) {
                        log.warn("No completed payments found for client ID: {}", clientId);
                        return ResponseEntity.ok(new ResponseWrapper<>(false, null, "No completed payments found for client ID: " + clientId, 404));
                    }

                    log.info("Completed payments successfully fetched for client ID: {}", clientId);
                    return ResponseEntity.ok(new ResponseWrapper<>(true, payments, "Completed payments correctly fetched.", 200));
                });
    }
}
