package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Order.OrderFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_cart_service.Utils.AddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final ClientCartService clientCartService;
    private final OrderService orderService;
    private final OrderFacadeService orderFacadeService;

    @Autowired
    public PurchaseServiceImpl(CartService cartService,
                               ClientCartService clientCartService,
                               OrderService orderService,
                               OrderFacadeService orderFacadeService) {
        this.clientCartService = clientCartService;
        this.orderService = orderService;
        this.orderFacadeService = orderFacadeService;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<ClientEcommerceDataDTO>> prepareClientData(Long clientId) {
        // Call the aggregateClientData method asynchronously and return the result
        return orderService.aggregateClientData(clientId);
    }

    @Override
    @Async("taskExecutor")
    public void processPurchase(ClientEcommerceDataDTO clientEcommerceDataDTO, Long cardId, Long addressId) {
        // Create Order And Clear Cart Async
        CompletableFuture<Void> clearOutCartFuture  = clientCartService.clearOutCart(clientEcommerceDataDTO.getCartDTO().getId());
        CompletableFuture<Long> orderFuture = orderService.createOrder(clientEcommerceDataDTO.getCartDTO(), clientEcommerceDataDTO.getClientDTO(), addressId);

        CompletableFuture.allOf(clearOutCartFuture, orderFuture);
        clearOutCartFuture.join();
        Long orderId = orderFuture.join();

        // Create Payment
        PaymentDTO paymentDTO = orderService.initPayment(clientEcommerceDataDTO, cardId, orderId);

        // Append Payment To Order
        CompletableFuture<Void> orderPaymentFuture = orderFacadeService.addPaymentIdByOrderId(paymentDTO.getId(), orderId);
        orderPaymentFuture.join();
        
        log.info("processPurchase -> purchase procceess completed succesfully");
    }
}


