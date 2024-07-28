package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import microservice.ecommerce_payment_service.Service.CardService;
import microservice.ecommerce_payment_service.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/payments")
@Validated
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final CardService cardService;

    @Autowired
    public PaymentController(PaymentService paymentService, CardService cardService) {
        this.paymentService = paymentService;
        this.cardService = cardService;
    }

    @PostMapping("/init")
    public ResponseEntity<ApiResponse<?>> initPayment(@Valid @RequestBody PaymentInsertDTO paymentInsertDTO) {
        logger.info("Initializing payment for client ID: {}", paymentInsertDTO.getClientId());

        boolean isClientValidated = cardService.validateClient(paymentInsertDTO.getClientId());
        if (!isClientValidated) {
            logger.warn("Client with ID {} not found.", paymentInsertDTO.getClientId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "User with ID " + paymentInsertDTO.getClientId() + " Not Found.", 404));
        }

        // Card Validation And Client
        if (paymentInsertDTO.getCardId() != null && "CARD".equals(paymentInsertDTO.getPaymentMethod())) {
            Optional<CardDTO> cardDTO = cardService.getCardById(paymentInsertDTO.getCardId());
            if (cardDTO.isEmpty()) {
                logger.warn("Card with ID {} not found.", paymentInsertDTO.getCardId());
                ApiResponse<Void> errorResponse = new ApiResponse<>(false ,null, "Card with Id " + paymentInsertDTO.getCardId() + " not found", 404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        }

        PaymentDTO paymentDTO = paymentService.initPaymentFromCart(paymentInsertDTO);
        logger.info("Payment successfully initiated for client ID: {}", paymentInsertDTO.getClientId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, paymentDTO, "Payment successfully initiated.", 201));
    }

    @PutMapping("/{paymentId}/validate")
    public ResponseEntity<ApiResponse<Void>> completePayment(@PathVariable Long paymentId, @RequestParam boolean isPaid) {
        logger.info("Validating payment with ID: {}", paymentId);

        Optional<PaymentDTO> optionalPaymentDTO = paymentService.getPaymentById(paymentId);
        if (optionalPaymentDTO.isEmpty()) {
            logger.warn("Payment with ID {} not found.", paymentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Payment with ID " + paymentId + " not found.", 404));
        }

        if (!isPaid) {
            paymentService.processPaymentFailed(paymentId);
            logger.info("Payment with ID {} marked as failed.", paymentId);
        } else {
            paymentService.processPaymentCompleted(paymentId);
            logger.info("Payment with ID {} marked as completed.", paymentId);
        }

        return ResponseEntity.ok(new ApiResponse<>(true, null, "Payment validation completed", 200));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentById(@PathVariable Long paymentId) {
        logger.info("Fetching payment with ID: {}", paymentId);

        Optional<PaymentDTO> optionalPaymentDTO = paymentService.getPaymentById(paymentId);
        if (optionalPaymentDTO.isEmpty()) {
            logger.warn("Payment with ID {} not found.", paymentId);
            ApiResponse<PaymentDTO> errorResponse = new ApiResponse<>(false, null, "Payment with ID " + paymentId + " not found.", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        PaymentDTO paymentDTO = optionalPaymentDTO.get();
        logger.info("Payment with ID {} successfully fetched.", paymentId);
        ApiResponse<PaymentDTO> successResponse = new ApiResponse<>(true, paymentDTO, "Payment correctly fetched.", 200);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }
}
