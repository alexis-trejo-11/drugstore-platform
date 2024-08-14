package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
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
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<ResponseEntity<ResponseWrapper<PaymentDTO>>> initPayment(@Valid @RequestBody PaymentInsertDTO paymentInsertDTO) {
        logger.info("Initializing payment for client ID: {}", paymentInsertDTO.getClientId());

        return cardService.validateClient(paymentInsertDTO.getClientId())
                .thenCompose(isClientValidated -> {
                    if (!isClientValidated) {
                        logger.warn("Client with ID {} not found.", paymentInsertDTO.getClientId());
                        return CompletableFuture.completedFuture(
                                ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new ResponseWrapper<>(false, null, "User with ID " + paymentInsertDTO.getClientId() + " Not Found.", 404))
                        );
                    }

                    // Card Validation And Client
                    if (paymentInsertDTO.getCardId() != null && "CARD".equals(paymentInsertDTO.getPaymentMethod())) {
                        return cardService.getCardById(paymentInsertDTO.getCardId())
                                .thenCompose(cardDTO -> {
                                    if (cardDTO.isEmpty()) {
                                        logger.warn("Card with ID {} not found.", paymentInsertDTO.getCardId());
                                        return CompletableFuture.completedFuture(
                                                ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                        .body(new ResponseWrapper<>(false, null, "Card with Id " + paymentInsertDTO.getCardId() + " not found", 404))
                                        );
                                    }

                                    // Proceed with payment initialization
                                    return paymentService.initPaymentFromCart(paymentInsertDTO)
                                            .thenApply(paymentDTO -> {
                                                logger.info("Payment successfully initiated for client ID: {}", paymentInsertDTO.getClientId());
                                                return ResponseEntity.status(HttpStatus.CREATED)
                                                        .body(new ResponseWrapper<>(true, paymentDTO, "Payment successfully initiated.", 201));
                                            });
                                });
                    } else {
                        // Proceed with payment initialization if no card ID is provided or payment method is not "CARD"
                        return paymentService.initPaymentFromCart(paymentInsertDTO)
                                .thenApply(paymentDTO -> {
                                    logger.info("Payment successfully initiated for client ID: {}", paymentInsertDTO.getClientId());
                                    return ResponseEntity.status(HttpStatus.CREATED)
                                            .body(new ResponseWrapper<>(true, paymentDTO, "Payment successfully initiated.", 201));
                                });
                    }
                });
    }

    @PutMapping("/{paymentId}/validate")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> completePayment(@PathVariable Long paymentId, @RequestParam boolean isPaid) {
        logger.info("Validating payment with ID: {}", paymentId);
        return paymentService.getPaymentById(paymentId)
                .thenCompose(optionalPaymentDTO -> {
                    if (optionalPaymentDTO.isEmpty()) {
                        logger.warn("Payment with ID {} not found.", paymentId);
                        return CompletableFuture.completedFuture(
                                ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new ResponseWrapper<>(false, null, "Payment with ID " + paymentId + " not found.", 404))
                        );

                    } else if (!optionalPaymentDTO.get().getPaymentStatus().equals("PENDING")) {
                        logger.warn("Payment with ID {} able to be processed.", paymentId);
                        return CompletableFuture.completedFuture(
                                ResponseEntity.status(HttpStatus.CONFLICT)
                                        .body(new ResponseWrapper<>(false, null, "Payment with ID " + paymentId + " already processed.", 404))
                        );
                    }

                    return paymentService.processPayment(paymentId, isPaid)
                            .thenApply(v -> ResponseEntity.ok(new ResponseWrapper<>(true, null, "Payment validation completed", 200)));
                    });
    }

    @GetMapping("/{paymentId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<PaymentDTO>>> getPaymentById(@PathVariable Long paymentId) {
        logger.info("Fetching payment with ID: {}", paymentId);

        return paymentService.getPaymentById(paymentId)
                .thenApply(optionalPaymentDTO -> {
                    if (optionalPaymentDTO.isEmpty()) {
                        logger.warn("Payment with ID {} not found.", paymentId);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, "Payment with ID " + paymentId + " not found.", 404));
                    }

                    PaymentDTO paymentDTO = optionalPaymentDTO.get();
                    logger.info("Payment with ID {} successfully fetched.", paymentId);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseWrapper<>(true, paymentDTO, "Payment correctly fetched.", 200));
                });
    }
}
