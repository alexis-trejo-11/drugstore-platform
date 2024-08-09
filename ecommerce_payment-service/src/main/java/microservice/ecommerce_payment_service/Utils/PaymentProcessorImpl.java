package microservice.ecommerce_payment_service.Utils;

import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderItemDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.ESale.ESaleFacadeImpl;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Order.OrderFacadeService;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_payment_service.Automappers.PaymentMapper;
import microservice.ecommerce_payment_service.Model.Card;
import microservice.ecommerce_payment_service.Model.Payment;
import microservice.ecommerce_payment_service.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PaymentProcessorImpl implements PaymentProcessor {
    private final PaymentRepository paymentRepository;
    private final OrderFacadeService orderFacadeService;
    private final ESaleFacadeImpl externalDigitalSale;
    private final PaymentMapper paymentMapper;

    @Autowired
    public PaymentProcessorImpl(PaymentRepository paymentRepository, PaymentMapper paymentMapper, OrderFacadeService orderFacadeService, ESaleFacadeImpl externalDigitalSale) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.orderFacadeService = orderFacadeService;
        this.externalDigitalSale = externalDigitalSale;
    }

    @Async("taskExecutor")
    @Override
    public void processPayment(Long paymentId, boolean isSuccess) {
        CompletableFuture.runAsync(() -> {
            Optional<PaymentDTO> optionalPaymentDTO = paymentRepository.findById(paymentId).map(paymentMapper::toDto);
            if (optionalPaymentDTO.isEmpty()) {
                throw new RuntimeException("Payment not found");
            }

            PaymentDTO paymentDTO = optionalPaymentDTO.get();
            CompletableFuture<OrderDTO> orderFuture = orderFacadeService.getOrderById(paymentDTO.getOrderId());

            orderFuture.thenCompose(orderDTO -> {
                if (orderDTO == null) {
                    throw new RuntimeException("Order not found");
                }

                return crateDigitalSaleAndGetId(paymentDTO, orderDTO.getItems())
                        .thenCompose(saleId -> {
                            if (isSuccess) {
                                return completeSuccessfulPayment(paymentId, orderDTO.getId(), saleId)
                                        .thenRun(() -> orderFacadeService.completeOrder(true, orderDTO.getId(), orderDTO.getAddressId(), orderDTO.getClientId()));
                            } else {
                                return completeNotPaidPayment(paymentId);
                            }
                        });
            }).join();
        });
    }



    @Async("taskExecutor")
    private CompletableFuture<Void> completeNotPaidPayment(Long paymentId) {
        return CompletableFuture.runAsync(() -> {
            paymentRepository.findById(paymentId).ifPresent(payment -> {
                payment.setPaymentDate(LocalDateTime.now());
                payment.setStatus(Payment.PaymentStatus.FAILURE);
                paymentRepository.save(payment);
            });
        });
    }
    @Override
    public void handleCartData(Payment payment, PaymentInsertDTO paymentInsertDTO) {
        Card card = new Card();
        card.setId(paymentInsertDTO.getCardId());
        payment.setCard(card);
    }

    @Async("taskExecutor")
    private CompletableFuture<Long> crateDigitalSaleAndGetId(PaymentDTO paymentDTO, List<OrderItemDTO> orderItemDTOS) {
        var dto = new DigitalSaleItemInsertDTO();
        dto.setPaymentDTO(paymentDTO);
        dto.setOrderItemDTOS(orderItemDTOS);
        return externalDigitalSale.makeDigitalSaleAndGetID(dto);
    }

    @Async("taskExecutor")
    private CompletableFuture<Void> completeSuccessfulPayment(Long paymentId, Long orderId, Long saleId) {
        return CompletableFuture.runAsync(() -> {
            paymentRepository.findById(paymentId).ifPresent(payment -> {
                payment.setPaymentDate(LocalDateTime.now());
                payment.setStatus(Payment.PaymentStatus.SUCCESS);
                payment.setOrderId(orderId);
                payment.setSaleId(saleId);
                paymentRepository.save(payment);
            });
        });
    }
}
