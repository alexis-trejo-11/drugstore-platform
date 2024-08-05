package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderItemDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.DigitalSale.ExternalDigitalSaleImpl;
import at.backend.drugstore.microservice.common_models.ExternalService.Order.ExternalOrderService;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_payment_service.Automappers.PaymentMapper;
import microservice.ecommerce_payment_service.Model.Card;
import microservice.ecommerce_payment_service.Model.Payment;
import microservice.ecommerce_payment_service.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ExternalOrderService externalOrderService;
    private final ExternalDigitalSaleImpl externalDigitalSale;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              PaymentMapper paymentMapper,
                              ExternalOrderService externalOrderService,
                              ExternalDigitalSaleImpl externalDigitalSale) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.externalOrderService = externalOrderService;
        this.externalDigitalSale = externalDigitalSale;
    }

    @Override
    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<Optional<PaymentDTO>> getPaymentById(Long paymentId) {
        return CompletableFuture.supplyAsync(() ->
                paymentRepository.findById(paymentId).map(paymentMapper::toDto)
        );
    }

    @Override
    @Async("taskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<List<PaymentDTO>> getCompletedPaymentsByClientId(Long clientId) {
        return CompletableFuture.supplyAsync(() ->
                paymentRepository.findCompletedPaymentsByClientId(clientId).stream()
                        .map(paymentMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> processPayment(Long paymentId, boolean isPaid) {
          if (!isPaid) {
              return processPaymentFailed(paymentId);
          }

          return processPaymentCompleted(paymentId);
    }


    @Async("taskExecutor")
    private CompletableFuture<Void> processPaymentAndOrder(PaymentDTO paymentDTO, OrderDTO orderDTO) {
        return crateDigitalSaleAndGetId(paymentDTO, orderDTO.getItems())
                .thenCompose(saleId -> {
                    completeSuccessfullPayment(paymentDTO.getId(), orderDTO.getId(), saleId);
                    return externalOrderService.completeOrder(true, orderDTO.getId(), orderDTO.getAddressId(), orderDTO.getClientId());
                });
    }

    @Override
    @Transactional
    public CompletableFuture<PaymentDTO> initPaymentFromCart(PaymentInsertDTO paymentInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            Payment payment = paymentMapper.toEntity(paymentInsertDTO);

            if (paymentInsertDTO.getCardId() != null) {
                handleCartData(payment, paymentInsertDTO);
            }

            payment = paymentRepository.saveAndFlush(payment);

            return paymentMapper.toDto(payment);
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
        }).thenCompose(externalDigitalSale::makeDigitalSaleAndGetID);
    }

    private CompletableFuture<Void> processPaymentFailed(Long paymentId) {
        return CompletableFuture.runAsync(() -> {
            Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);

            completeNotPaidPayment(paymentId);
            var orderFuture = externalOrderService.completeOrder(false, optionalPayment.get().getOrderId(), null, null);
            orderFuture.join();
        });
    }

    private void completeSuccessfullPayment(Long paymentId, Long orderId, Long saleId) {
        paymentRepository.findById(paymentId).ifPresent(payment -> {
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            payment.setOrderId(orderId);
            payment.setSaleId(saleId);
            paymentRepository.save(payment);
        });
    }

    private void completeNotPaidPayment(Long paymentId) {
        paymentRepository.findById(paymentId).ifPresent(payment -> {
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatus(Payment.PaymentStatus.FAILURE);
            paymentRepository.save(payment);
        });
    }

    private CompletableFuture<Void> processPaymentCompleted(Long paymentId) {
        return CompletableFuture.supplyAsync(() -> paymentRepository.findById(paymentId))
                .thenApply(paymentOpt -> paymentOpt.map(paymentMapper::toDto)
                        .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentId)))

                .thenCompose(paymentDTO ->
                        externalOrderService.getOrderById(paymentDTO.getOrderId())
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

    private void handleCartData(Payment payment, PaymentInsertDTO paymentInsertDTO) {
        Card card = new Card();
        card.setId(paymentInsertDTO.getCardId());
        payment.setCard(card);
    }
}