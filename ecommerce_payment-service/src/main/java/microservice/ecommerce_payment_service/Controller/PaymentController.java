package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import microservice.ecommerce_payment_service.Service.CardService;
import microservice.ecommerce_payment_service.Service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/v1/api/payments")
@Validated
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final CardService cardService;
    private final ExternalClientService externalClientService;

    @Autowired
    public PaymentController(PaymentService paymentService, CardService cardService, ExternalClientService externalClientService) {
        this.paymentService = paymentService;
        this.cardService = cardService;
        this.externalClientService = externalClientService;

    }

    @PostMapping("/init")
    public ResponseEntity<ResponseWrapper<Void>> initPayment(@Valid @RequestBody PaymentInsertDTO paymentInsertDTO,
                                                             BindingResult bindingResult) {
        try {
            // Global exception handler will handle validation errors
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(new ResponseWrapper<>(null, "Validation error."));
            }

            // Card Validation
            if (paymentInsertDTO.getCardId() != null && "CARD".equals(paymentInsertDTO.getPaymentMethod())) {
                CardDTO cardDTO = cardService.getCardById(paymentInsertDTO.getCardId());
                if (cardDTO == null) {
                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, "Card with Id " + paymentInsertDTO.getCardId() + " not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                }
            }

            // Client Validation
            var clientDTOResult = externalClientService.findClientById(paymentInsertDTO.getClientId());
            if (!clientDTOResult.isSuccess()) {
                ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, clientDTOResult.getErrorMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Proceed with payment initialization
            paymentService.initPaymentFromCart(paymentInsertDTO);

            ResponseWrapper<Void> response = new ResponseWrapper<>(null, "Payment successfully init.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error initializing payment: ", e);
            String errorMessage = "An error occurred while creating payment.";
            ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get payment details by payment ID.
     *
     * @param paymentId the ID of the payment to retrieve
     * @return ResponseEntity with status and response wrapper containing the payment details
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<ResponseWrapper<PaymentDTO>> getPaymentById(
            @PathVariable @Positive @Min(1) @NotNull Long paymentId) {
        try {
            PaymentDTO paymentDTO = paymentService.getPaymentById(paymentId);
            if (paymentDTO == null) {
                ResponseWrapper<PaymentDTO> errorResponse = new ResponseWrapper<>(null, "Payment with ID " + paymentId + " not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            ResponseWrapper<PaymentDTO> response = new ResponseWrapper<>(paymentDTO, "Payment correctly fetched.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching payment by ID: ", e);
            String errorMessage = "An error occurred while fetching the payment with ID: " + paymentId;
            ResponseWrapper<PaymentDTO> errorResponse = new ResponseWrapper<>(null, errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get completed payments by client ID.
     *
     * @param clientId the ID of the client to retrieve completed payments for
     * @return ResponseEntity with status and response wrapper containing the list of completed payments
     */
    @GetMapping("/client/{clientId}/completed")
    public ResponseEntity<ResponseWrapper<List<PaymentDTO>>> getCompletedPaymentsByClientId(
            @PathVariable @Positive @Min(1) @NotNull Long clientId) {
        try {
            List<PaymentDTO> payments = paymentService.getCompletedPaymentsByClientId(clientId);
            ResponseWrapper<List<PaymentDTO>> response = new ResponseWrapper<>(payments, "Completed payments correctly fetched.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching completed payments by client ID: ", e);
            String errorMessage = "An error occurred while fetching completed payments for client with ID: " + clientId;
            ResponseWrapper<List<PaymentDTO>> errorResponse = new ResponseWrapper<>(null, errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Validate a payment.
     *
     * @param paymentId    the ID of the payment to validate
     * @param isPaymentPaid boolean indicating if the payment is paid
     * @return ResponseEntity with status and response wrapper containing the validated payment details
     */
    @PostMapping("/{paymentId}/validate")
    public ResponseEntity<ResponseWrapper<PaymentDTO>> validPayment(
            @Valid @PathVariable @Positive @Min(1) @NotNull Long paymentId,
            @RequestParam boolean isPaymentPaid) {
        try {
            // Valid Payment
            PaymentDTO paymentDTO = paymentService.validPayment(paymentId, isPaymentPaid);
            if (paymentDTO == null) {
                // Not Found Payment
                ResponseWrapper<PaymentDTO> errorResponse = new ResponseWrapper<>(null, "Payment with ID " + paymentId + " not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Return Success
            ResponseWrapper<PaymentDTO> response = new ResponseWrapper<>(paymentDTO, "Payment validation completed. Order created will be shipping soon!.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error validating payment: ", e);
            String errorMessage = "An error occurred while validating the payment with ID: " + paymentId;
            ResponseWrapper<PaymentDTO> errorResponse = new ResponseWrapper<>(null, errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
