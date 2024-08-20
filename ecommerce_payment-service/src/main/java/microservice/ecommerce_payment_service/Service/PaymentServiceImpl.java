package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentInsertDTO;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_payment_service.Mappers.PaymentMapper;
import microservice.ecommerce_payment_service.Model.Payment;
import microservice.ecommerce_payment_service.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentDomainService paymentDomainService;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              PaymentMapper paymentMapper,
                              PaymentDomainService paymentDomainService) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.paymentDomainService = paymentDomainService;
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
              return paymentDomainService.processPaymentFailed(paymentId);
          }

          return paymentDomainService.processPaymentCompleted(paymentId);
    }


    @Override
    @Transactional
    public CompletableFuture<PaymentDTO> initPaymentFromCart(PaymentInsertDTO paymentInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            Payment payment = paymentMapper.toEntity(paymentInsertDTO);

            if (paymentInsertDTO.getCardId() != null) {
                paymentDomainService.handleCartData(payment, paymentInsertDTO);
            }

            payment = paymentRepository.saveAndFlush(payment);

            return paymentMapper.toDto(payment);
        });
    }

}