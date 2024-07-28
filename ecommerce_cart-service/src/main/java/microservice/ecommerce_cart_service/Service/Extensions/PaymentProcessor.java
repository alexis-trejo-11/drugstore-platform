package microservice.ecommerce_cart_service.Service.Extensions;

import at.backend.drugstore.microservice.common_models.DTO.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Order.ExternalOrderService;
import at.backend.drugstore.microservice.common_models.ExternalService.Payment.ExternalPaymentService;
import microservice.ecommerce_cart_service.Mappers.AfterwardMapper;
import microservice.ecommerce_cart_service.Model.Afterward;
import microservice.ecommerce_cart_service.Model.Cart;
import microservice.ecommerce_cart_service.Model.CartItem;
import microservice.ecommerce_cart_service.Repository.AfterwardsRepository;
import microservice.ecommerce_cart_service.Repository.CartRepository;
import microservice.ecommerce_cart_service.Service.Factory.PaymentDTOFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PaymentProcessor {
    private final ExternalPaymentService externalPaymentService;
    private final ExternalOrderService externalOrderService;
    private final AfterwardsRepository afterwardsRepository;
    private final AfterwardMapper afterwardMapper;
    private  final CartRepository cartRepository;

    @Autowired
    public PaymentProcessor(ExternalPaymentService externalPaymentService,
                            ExternalOrderService externalOrderService,
                            AfterwardsRepository afterwardsRepository, AfterwardMapper afterwardMapper, CartRepository cartRepository) {
        this.externalPaymentService = externalPaymentService;
        this.externalOrderService = externalOrderService;
        this.afterwardsRepository = afterwardsRepository;
        this.afterwardMapper = afterwardMapper;
        this.cartRepository = cartRepository;
    }

    public void processPayment(ClientEcommerceDataDTO clientData, Long cardId, Long orderId) {
        CardDTO cardDTO = clientData.getCardDTOS().stream()
                .filter(card -> card.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Card Not Found"));

        PaymentInsertDTO paymentInsertDTO = PaymentDTOFactory.createPaymentInsertDTO(
                clientData.getCartDTO(), clientData.getClientDTO(), cardDTO, orderId);

        PaymentDTO paymentDTO = externalPaymentService.initPayment(paymentInsertDTO);
        externalOrderService.addPaymentIdByOrderId(paymentDTO.getId(), orderId);
    }

    private void processReturnToCart(Long clientId, Long productId, Cart cart) {
        Optional<Afterward> optionalAfterward = afterwardsRepository.findByClientIdAndProductId(clientId, productId);
        Afterward afterward = optionalAfterward.get();
        afterwardsRepository.deleteById(afterward.getId());
        CartItem cartItem = afterwardMapper.entityToCartItem(afterward);

        List<CartItem> cartItems = cart.getCartItems();
        cartItems.add(cartItem);
        cartRepository.saveAndFlush(cart);
    }
}