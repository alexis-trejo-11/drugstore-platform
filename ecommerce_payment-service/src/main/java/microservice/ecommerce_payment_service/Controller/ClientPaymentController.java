package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import microservice.ecommerce_payment_service.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/user-payments")
public class ClientPaymentController {

    private final PaymentService paymentService;

    public ClientPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/client/{clientId}/completed")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getCompletedPaymentsByClientId(Long clientId) {
        List<PaymentDTO> payments = paymentService.getCompletedPaymentsByClientId(clientId);
        return ResponseEntity.ok(new ApiResponse<>(true, payments, "Completed payments correctly fetched.", 200));
    }
}
