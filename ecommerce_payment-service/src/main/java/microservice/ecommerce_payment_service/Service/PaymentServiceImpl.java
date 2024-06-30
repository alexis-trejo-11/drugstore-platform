package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import microservice.ecommerce_payment_service.Automappers.PaymentMapper;
import microservice.ecommerce_payment_service.Model.Payment;
import microservice.ecommerce_payment_service.Repository.CardRepository;
import microservice.ecommerce_payment_service.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CardRepository cardRepository;
    private final PaymentMapper paymentMapper;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, CardRepository cardRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.cardRepository = cardRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    @Async
    @Transactional
    public PaymentDTO getPaymentById(Long paymentId) {
        try {
            Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
            if (optionalPayment.isEmpty()) {
                return null;
            }

            Payment payment = optionalPayment.get();
            return paymentMapper.toDto(payment);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch completed payments for payment with ID: " + paymentId, e);
        }
    }

    @Override
    @Async
    @Transactional(readOnly = true)
    public List<PaymentDTO> getCompletedPaymentsByClientId(Long clientId) {
        try {
            List<Payment> payments = paymentRepository.findCompletedPaymentsByClientId(clientId);
            return payments.stream()
                    .map(paymentMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Handle exceptions
            throw new RuntimeException("Failed to fetch completed payments for client with ID: " + clientId, e);
        }
    }

    @Override
    @Async
    @Transactional
    public PaymentDTO validPayment(Long paymentId, boolean isPaymentPaid) {
        try {
            Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
            if (optionalPayment.isEmpty()) {
                return null;
            }

            Payment payment = new Payment();

            if (!isPaymentPaid) {
                payment.setPaymentDate(LocalDateTime.now());
                payment.setStatus(Payment.PaymentStatus.FAILURE);
                paymentRepository.save(payment);
            } else {
                payment.setPaymentDate(LocalDateTime.now());
                payment.setStatus(Payment.PaymentStatus.SUCCESS);
                paymentRepository.save(payment);
            }

            return paymentMapper.toDto(payment);
        } catch (Exception e) {
            throw new RuntimeException("Failed to validate payment", e);
        }
    }


    @Override
    @Async
    @Transactional
    public void initPaymentFromCart(PaymentInsertDTO paymentInsertDTO) {
        try {
            Payment payment = paymentMapper.toEntity(paymentInsertDTO);

            paymentRepository.saveAndFlush(payment);
        } catch (Exception e) {
            throw new RuntimeException("Failed to validate payment", e);
        }
    }

}
