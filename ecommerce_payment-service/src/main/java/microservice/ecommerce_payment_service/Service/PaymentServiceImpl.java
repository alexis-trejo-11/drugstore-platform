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
import microservice.ecommerce_payment_service.Controller.PaymentController;
import microservice.ecommerce_payment_service.Model.Card;
import microservice.ecommerce_payment_service.Model.Payment;
import microservice.ecommerce_payment_service.Repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ExternalOrderService externalOrderService;
    private final ExternalDigitalSaleImpl externalDigitalSale;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentMapper paymentMapper, ExternalOrderService externalOrderService, ExternalDigitalSaleImpl externalDigitalSale) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.externalOrderService = externalOrderService;
        this.externalDigitalSale = externalDigitalSale;
    }

    @Override
    @Async
    @Transactional
    public Optional<PaymentDTO> getPaymentById(Long paymentId) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        return optionalPayment.map(paymentMapper::toDto);
    }

    @Override
    @Async
    @Transactional(readOnly = true)
    public List<PaymentDTO> getCompletedPaymentsByClientId(Long clientId) {
        List<Payment> payments = paymentRepository.findCompletedPaymentsByClientId(clientId);
        return payments.stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Async
    @Transactional
    public void processPaymentCompleted(Long paymentId) {
        Optional<PaymentDTO> optionalPaymentDTO = paymentRepository.findById(paymentId)
                .map(paymentMapper::toDto);
        if (optionalPaymentDTO.isEmpty()) {
            throw new RuntimeException(optionalPaymentDTO.toString());
        }

        PaymentDTO paymentDTO = optionalPaymentDTO.get();

        Optional<OrderDTO> optionalOrderDTO = externalOrderService.getOrderById(paymentDTO.getOrderId());
        if (optionalOrderDTO.isEmpty()) {
            throw new RuntimeException(optionalOrderDTO.toString());
        }
        OrderDTO orderDTO = optionalOrderDTO.get();

        Long saleId = crateDigitalSaleAndGetId(paymentDTO, orderDTO.getItems());
        completeSuccessfullPayment(paymentId, orderDTO.getId(), saleId);
        externalOrderService.completeOrder(true, orderDTO.getId(), orderDTO.getAddressId(), orderDTO.getClientId());
    }


    @Override
    @Async
    @Transactional
    public void processPaymentFailed(Long paymentId) {
        Optional<PaymentDTO> optionalPaymentDTO =  paymentRepository.findById(paymentId)
                .map(paymentMapper::toDto);
        completeNotPaidPayment(paymentId);
    }

    @Override
    @Async
    @Transactional
    public PaymentDTO initPaymentFromCart(PaymentInsertDTO paymentInsertDTO) {
        Payment payment = paymentMapper.toEntity(paymentInsertDTO);

        if (paymentInsertDTO.getCardId() != null) {
            handleCartData(payment, paymentInsertDTO);
        }

        paymentRepository.saveAndFlush(payment);

        return paymentMapper.toDto(payment);
    }

    private Long crateDigitalSaleAndGetId(PaymentDTO paymentDTO, List<OrderItemDTO> orderItemDTOS) {
        var dto = new DigitalSaleItemInsertDTO();
        dto.setPaymentDTO(paymentDTO);
        dto.setOrderItemDTOS(orderItemDTOS);
        return externalDigitalSale.makeDigitalSaleAndGetID(dto);
    }

    private void completeSuccessfullPayment(Long paymentId, Long orderId, Long saleId) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        Payment payment = optionalPayment.get();

        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setOrderId(orderId);
        payment.setSaleId(saleId);
        paymentRepository.save(payment);
    }

    private void completeNotPaidPayment(Long paymentId) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        Payment payment = optionalPayment.get();
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(Payment.PaymentStatus.FAILURE);
        paymentRepository.save(payment);
    }

    private void handleCartData(Payment payment, PaymentInsertDTO paymentInsertDTO) {
        Card card = new Card();
        card.setId(paymentInsertDTO.getCardId());
        payment.setCard(card);
    }
}
