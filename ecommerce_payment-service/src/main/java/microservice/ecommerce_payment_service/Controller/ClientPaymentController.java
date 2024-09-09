package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_classes.Security.AuthSecurity;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_payment_service.Service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/api/user-payments")
@Tag(name = "Drugstore Microservice API (E-Payment Service)", description = "Service for managing client payments")
public class ClientPaymentController {

    private final PaymentService paymentService;
    private final AuthSecurity authSecurity;

    public ClientPaymentController(PaymentService paymentService, AuthSecurity authSecurity) {
        this.paymentService = paymentService;
        this.authSecurity = authSecurity;
    }

    @Operation(summary = "Retrieve completed payments by client ID",
            description = "Fetches completed payments for a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Completed payments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found or no completed payments found")
    })
    @GetMapping("/client/{clientId}/completed")
    public ResponseEntity<ResponseWrapper<Page<PaymentDTO>>> getCompletedPaymentsByClientId(HttpServletRequest request,
                                                                                            @RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "10") int size) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("Fetching card for client ID: {}", clientId);

        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentDTO> paymentDTOS = paymentService.getCompletedPaymentsByClientId(clientId, pageable);
        log.info("Completed payments successfully fetched for client ID: {}", clientId);

        return ResponseEntity.ok(ResponseWrapper.found(paymentDTOS, "Payments"));
    }
}
