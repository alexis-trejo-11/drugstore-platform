package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentInsertDTO;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_payment_service.Mappers.PaymentMapper;
import microservice.ecommerce_payment_service.Model.Payment;
import microservice.ecommerce_payment_service.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PaymentDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) { return null;}

        return paymentMapper.entityToDto(payment);
    }

    @Override
    public Page<PaymentDTO> getCompletedPaymentsByClientId(Long clientId, Pageable pageable) {
        Page<Payment> payments =  paymentRepository.findCompletedPaymentsByClientId(clientId, pageable);
        return payments.map(paymentMapper::entityToDto);

    }

    @Override
    public void processPayment(Long paymentId, boolean isPaid) {
        if (!isPaid) {
              paymentDomainService.processPaymentFailed(paymentId);
        } else {
            paymentDomainService.processPaymentCompleted(paymentId);
        }
    }

    @Override
    @Transactional
    public PaymentDTO initPaymentFromCart(PaymentInsertDTO paymentInsertDTO) {
            Payment payment = paymentMapper.toEntity(paymentInsertDTO);

            if (paymentInsertDTO.getCardId() != null) {
                paymentDomainService.handleCartData(payment, paymentInsertDTO);
            }

            payment = paymentRepository.saveAndFlush(payment);

            return paymentMapper.entityToDto(payment);
    }

    @Override
    public boolean validExistingPayment(Long paymentId) {
        return paymentRepository.findById(paymentId).isPresent();
    }

}