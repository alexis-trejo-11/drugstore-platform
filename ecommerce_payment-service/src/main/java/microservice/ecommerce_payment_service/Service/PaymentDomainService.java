package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.DigitalSaleItemInsertDTO;

import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.ESale.ESaleFacadeService;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Order.OrderFacadeService;
import microservice.ecommerce_payment_service.Automappers.PaymentMapper;
import microservice.ecommerce_payment_service.Model.Card;
import microservice.ecommerce_payment_service.Model.Payment;
import microservice.ecommerce_payment_service.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentDomainService {

    private final OrderFacadeService orderFacadeService;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ESaleFacadeService eSaleFacadeService;

    public PaymentDomainService(@Qualifier("orderFacadeService") OrderFacadeService orderFacadeService,
                                PaymentRepository paymentRepository,
                                PaymentMapper paymentMapper,
                                ESaleFacadeService eSaleFacadeService) {
        this.orderFacadeService = orderFacadeService;
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.eSaleFacadeService = eSaleFacadeService;
    }

    public CompletableFuture<Void> processPaymentFailed(Long paymentId) {
        return CompletableFuture.runAsync(() -> {
            Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);

            completeNotPaidPayment(paymentId);
            var orderFuture = orderFacadeService.completeOrder(false, optionalPayment.get().getOrderId(), null, null);
            orderFuture.join();
        });
    }

    public void completeSuccessfullPayment(Long paymentId, Long orderId, Long saleId) {
        paymentRepository.findById(paymentId).ifPresent(payment -> {
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setOrderId(orderId);
            payment.setSaleId(saleId);
            paymentRepository.save(payment);
        });
    }

    public void completeNotPaidPayment(Long paymentId) {
        paymentRepository.findById(paymentId).ifPresent(payment -> {
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatus(Payment.PaymentStatus.FAILURE);
            paymentRepository.save(payment);
        });
    }

    public CompletableFuture<Void> processPaymentCompleted(Long paymentId) {
        return CompletableFuture.supplyAsync(() -> paymentRepository.findById(paymentId))
                .thenApply(paymentOpt -> paymentOpt.map(paymentMapper::toDto)
                        .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentId)))

                .thenCompose(paymentDTO ->
                        orderFacadeService.getOrderById(paymentDTO.getOrderId())
                                .thenApply(orderDTO -> {
                                    if (orderDTO == null) {
                                        throw new EntityNotFoundException("Order not found for payment id: " + paymentId);
                                    }

                                    CompletableFuture<Void> future = processPaymentAndOrder(paymentDTO, orderDTO);
                                    future.join();
                                    return null;
                                })
                );
    }

    public void handleCartData(Payment payment, PaymentInsertDTO paymentInsertDTO) {
        Card card = new Card();
        card.setId(paymentInsertDTO.getCardId());
        payment.setCard(card);
    }

    @Async("taskExecutor")
    private CompletableFuture<Void> processPaymentAndOrder(PaymentDTO paymentDTO, OrderDTO orderDTO) {
        return crateDigitalSaleAndGetId(paymentDTO, orderDTO.getItems())
                .thenCompose(saleId -> {
                    completeSuccessfullPayment(paymentDTO.getId(), orderDTO.getId(), saleId);
                    return orderFacadeService.completeOrder(true, orderDTO.getId(), orderDTO.getAddressId(), orderDTO.getClientId());
                });
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Long> crateDigitalSaleAndGetId(PaymentDTO paymentDTO, List<OrderItemDTO> orderItemDTOS) {
        return CompletableFuture.supplyAsync(() -> {
            var dto = new DigitalSaleItemInsertDTO();
            dto.setPaymentDTO(paymentDTO);
            dto.setOrderItemDTOS(orderItemDTOS);
            return dto;
        }).thenCompose(eSaleFacadeService::makeDigitalSaleAndGetID);
    }
}
