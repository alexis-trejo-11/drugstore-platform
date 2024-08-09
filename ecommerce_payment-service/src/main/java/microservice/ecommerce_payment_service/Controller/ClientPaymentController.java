package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_models.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import microservice.ecommerce_payment_service.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1/api/user-payments")
public class ClientPaymentController {

    private final PaymentService paymentService;

    public ClientPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/client/{clientId}/completed")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<PaymentDTO>>>> getCompletedPaymentsByClientId(@PathVariable Long clientId) {
        return paymentService.getCompletedPaymentsByClientId(clientId)
                .thenApply(payments ->
                        ResponseEntity.ok(new ResponseWrapper<>(true, payments, "Completed payments correctly fetched.", 200))
                );
    }
}