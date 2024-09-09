package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_payment_service.Service.CardService;
import microservice.ecommerce_payment_service.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/v1/api/payments")
@Tag(name = "Drugstore Microservice API (E-Payment Service)", description = "Service for managing payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final CardService cardService;

    @Autowired
    public PaymentController(PaymentService paymentService, CardService cardService) {
        this.paymentService = paymentService;
        this.cardService = cardService;
    }

    @Operation(summary = "Initialize a payment",
            description = "Initiates a payment for a client. Validates client and card if necessary.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment successfully initiated"),
            @ApiResponse(responseCode = "404", description = "Client or card not found"),
            @ApiResponse(responseCode = "400", description = "Invalid payment request")
    })
    @PostMapping("/init")
    public ResponseEntity<ResponseWrapper<PaymentDTO>> initPayment(@Valid @RequestBody PaymentInsertDTO paymentInsertDTO) {
        log.info("Initializing payment for client ID: {}", paymentInsertDTO.getClientId());

        if (paymentInsertDTO.getCardId() != null && "CARD".equals(paymentInsertDTO.getPaymentMethod())) {
            boolean isCardValidated = cardService.validateExistingCard(paymentInsertDTO.getCardId());
            if (!isCardValidated) {
                log.warn("Card with ID {} not found.", paymentInsertDTO.getCardId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Card with Id " + paymentInsertDTO.getCardId() + " not found", 404));
            }
            CardDTO cardDTO = cardService.getCardById(paymentInsertDTO.getCardId());

            PaymentDTO paymentDTO = paymentService.initPaymentFromCart(paymentInsertDTO);
            log.info("initPayment -> Payment successfully initiated for client ID: {}", paymentInsertDTO.getClientId());

            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper<>(true, paymentDTO, "Payment successfully initiated.", 201));
        } else {
            PaymentDTO paymentDTO = paymentService.initPaymentFromCart(paymentInsertDTO);
            log.info("initPayment -> Payment successfully initiated for client ID: {}", paymentInsertDTO.getClientId());

            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper<>(true, paymentDTO, "Payment successfully initiated.", 201));
        }
    }

    @Operation(summary = "Validate a payment",
            description = "Completes a payment process by validating it based on its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment validation completed"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "409", description = "Payment already processed")
    })
    @PutMapping("/{paymentId}/validate")
    public ResponseEntity<ResponseWrapper<Void>> completePayment(@PathVariable Long paymentId, @RequestParam boolean isPaid) {
        log.info("Validating payment with ID: {}", paymentId);

        boolean isPaymentValidated = paymentService.validExistingPayment(paymentId);
        if (!isPaymentValidated) {
            log.warn("Payment with ID {} not found.", paymentId);
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Payment", "Id"));
        }

        PaymentDTO paymentDTO = paymentService.getPaymentById(paymentId);
        if (paymentDTO.getPaymentStatus().equals("PENDING")) {
            log.warn("Payment with ID {} already processed.", paymentId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseWrapper.error("Payment with ID " + paymentId + " already processed.", 409));
        }

        paymentService.processPayment(paymentId, isPaid);
        return ResponseEntity.ok(ResponseWrapper.ok("Payment Validation", "Complete"));
    }

    @Operation(summary = "Fetch payment by ID",
            description = "Retrieves payment details based on the payment ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/{paymentId}")
    public ResponseEntity<ResponseWrapper<PaymentDTO>> getPaymentById(@PathVariable Long paymentId) {
        log.info("Fetching payment with ID: {}", paymentId);
        boolean isPaymentValidated = paymentService.validExistingPayment(paymentId);
        if (!isPaymentValidated) {
            log.warn("getPaymentById -> Payment with ID {} not found.", paymentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Payment", "Id"));
        }

        PaymentDTO paymentDTO = paymentService.getPaymentById(paymentId);
        log.info("Payment with ID {} successfully fetched.", paymentId);

        return ResponseEntity.ok(ResponseWrapper.found(paymentDTO, "Payment"));
    }
}
