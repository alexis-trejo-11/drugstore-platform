package io.github.alexisTrejo11.drugstore.payments.core.application;

import io.github.alexisTrejo11.drugstore.payments.core.application.dto.request.InitiatePaymentRequest;
import io.github.alexisTrejo11.drugstore.payments.core.application.dto.request.RefundRequest;
import io.github.alexisTrejo11.drugstore.payments.core.application.dto.request.StripeWebhookRequest;
import io.github.alexisTrejo11.drugstore.payments.core.application.dto.response.PaymentResponse;
import io.github.alexisTrejo11.drugstore.payments.core.application.dto.response.SaleResponse;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.PaymentMethod;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.*;
import io.github.alexisTrejo11.drugstore.payments.core.port.input.PaymentApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import io.github.alexisTrejo11.drugstore.payments.core.domain.exception.PaymentBusinessRuleException;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Payment;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Sale;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.params.CreatePaymentParams;
import io.github.alexisTrejo11.drugstore.payments.core.domain.service.PaymentDomainService;
import io.github.alexisTrejo11.drugstore.payments.core.port.StripeGatewayPort;
import io.github.alexisTrejo11.drugstore.payments.core.port.output.PaymentRepository;
import io.github.alexisTrejo11.drugstore.payments.core.port.output.SaleRepository;

/**
 * Application Service — orchestrates the payment and sale workflows.
 *
 * Responsibilities:
 * - Coordinate domain services, repositories and external ports (Stripe)
 * - Manage transaction boundaries
 * - Map between DTOs and domain objects
 * - Does NOT contain business logic (that lives in domain services and aggregates)
 */
@Service
@Transactional
public class PaymentApplicationServiceImpl implements PaymentApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentApplicationServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final SaleRepository saleRepository;
    private final PaymentDomainService paymentDomainService;
    private final StripeGatewayPort stripeGateway;

    @Autowired
    public PaymentApplicationServiceImpl(PaymentRepository paymentRepository,
                                      SaleRepository saleRepository,
                                      PaymentDomainService paymentDomainService,
                                      StripeGatewayPort stripeGateway) {
        this.paymentRepository = paymentRepository;
        this.saleRepository = saleRepository;
        this.paymentDomainService = paymentDomainService;
        this.stripeGateway = stripeGateway;
    }


    /**
     * Initiates a payment flow:
     * 1. Creates a Payment aggregate in PENDING state
     * 2. Validates no duplicate payment for this order
     * 3. Creates a Stripe PaymentIntent
     * 4. Transitions payment to PROCESSING
     * 5. Returns clientSecret for frontend to confirm with Stripe.js
     *
     * The payment is NOT completed here — Stripe will call our webhook
     * when the customer confirms the payment on the frontend.
     */
    public PaymentResponse initiatePayment(InitiatePaymentRequest request) {
        logger.info("Initiating payment | orderId={} customerId={} amount={} {}",
                request.orderId(), request.customerId(), request.amount(), request.currency());

        // Map request to domain objects
        CreatePaymentParams params = new CreatePaymentParams(
                OrderID.of(request.orderId()),
                CustomerID.of(request.customerId()),
                Money.of(request.amount(), request.currency()),
                PaymentMethod.valueOf(request.paymentMethod().toUpperCase())
        );

        // Create payment aggregate — starts as PENDING
        Payment payment = Payment.create(params);

        // Business rule: no duplicate payments for the same order
        paymentDomainService.validateNoDuplicatePayment(payment);

        // Persist PENDING payment before calling Stripe (safe ordering)
        paymentRepository.save(payment);

        // Call Stripe to create PaymentIntent — use orderId as idempotency key
        StripeGatewayPort.StripePaymentIntentResult stripeResult = stripeGateway.createPaymentIntent(
                payment.getAmount(), payment.getOrderId().value()
        );

        // Transition to PROCESSING with gateway reference
        paymentDomainService.startProcessing(payment, stripeResult.toGatewayRef());

        logger.info("Payment initiated | id={} paymentIntentId={}",
                payment.getId().value(), stripeResult.paymentIntentId());

        // Return with clientSecret so frontend can call stripe.confirmPayment()
        return PaymentResponse.from(payment, stripeResult.clientSecret());
    }

    // Handle Stripe Webhook

    /**
     * Processes Stripe webhook events.
     * This is the central entry point for all async payment state transitions.
     *
     * The infrastructure layer (WebhookController) is responsible for:
     * - Verifying Stripe webhook signature
     * - Parsing the raw Stripe event
     * - Mapping to StripeWebhookRequest
     *
     * This service handles the business flow after verification.
     */
    public void handleStripeWebhook(StripeWebhookRequest webhook) {
        logger.info("Processing Stripe webhook | eventType={} paymentIntentId={}",
                webhook.eventType(), webhook.paymentIntentId());

        if (webhook.isSucceeded()) {
            handlePaymentSucceeded(webhook.paymentIntentId(), webhook.chargeId());
        } else if (webhook.isFailed()) {
            handlePaymentFailed(webhook.paymentIntentId(), webhook.failureReason());
        } else {
            logger.debug("Unhandled Stripe webhook event type: {}", webhook.eventType());
        }
    }

    private void handlePaymentSucceeded(String paymentIntentId, String chargeId) {
        Payment payment = findPaymentByGatewayIntentId(paymentIntentId);

        // Idempotency: Stripe may send the same event more than once
        if (payment.isCompleted()) {
            logger.warn("Payment already completed, skipping webhook | id={}", payment.getId().value());
            return;
        }

        // Confirm payment — this triggers PaymentCompletedEvent → Sale is created
        paymentDomainService.confirmPayment(payment, chargeId);

        logger.info("Stripe webhook handled: payment succeeded | paymentId={} chargeId={}",
                payment.getId().value(), chargeId);
    }

    private void handlePaymentFailed(String paymentIntentId, String reason) {
        Payment payment = findPaymentByGatewayIntentId(paymentIntentId);
        paymentDomainService.failPayment(payment, reason != null ? reason : "Payment declined");

        logger.info("Stripe webhook handled: payment failed | paymentId={}", payment.getId().value());
    }

    // Refunds

    /**
     * Processes a refund (full or partial) by:
     * 1. Loading payment and its associated sale
     * 2. Calling Stripe to issue the refund
     * 3. Delegating business logic to PaymentDomainService
     *    which updates payment status and publishes event
     * 4. SaleDomainService reacts to the event and updates the sale
     */
    public PaymentResponse processRefund(RefundRequest request) {
        logger.info("Processing refund | paymentId={} isPartial={} amount={}",
                request.paymentId(), request.isPartial(), request.amount());

        Payment payment = paymentRepository.findById(PaymentID.of(request.paymentId()))
                .orElseThrow(() -> new PaymentBusinessRuleException(
                        "Payment not found: " + request.paymentId()));

        Sale sale = saleRepository.findByPaymentId(payment.getId())
                .orElseThrow(() -> new PaymentBusinessRuleException(
                        "Sale not found for payment: " + request.paymentId()));

        String chargeId = payment.getGatewayRef().gatewayChargeId();
        if (chargeId == null || chargeId.isBlank()) {
            throw new PaymentBusinessRuleException(
                    "Cannot refund payment without a gateway charge ID: " + payment.getId().value());
        }

        if (request.isPartial()) {
            Money refundAmount = Money.of(request.amount(), payment.getAmount().currency());
            String gatewayRefundId = stripeGateway.partialRefundCharge(chargeId, refundAmount, request.reason());
            paymentDomainService.processPartialRefund(payment, sale, refundAmount, request.reason(), gatewayRefundId);
        } else {
            String gatewayRefundId = stripeGateway.refundCharge(chargeId, request.reason());
            paymentDomainService.processFullRefund(payment, sale, request.reason(), gatewayRefundId);
        }

        logger.info("Refund processed | paymentId={} saleId={}",
                payment.getId().value(), sale.getId().value());

        return PaymentResponse.from(payment);
    }

    // Queries

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(String paymentId) {
        Payment payment = paymentRepository.findById(PaymentID.of(paymentId))
                .orElseThrow(() -> new PaymentBusinessRuleException(
                        "Payment not found: " + paymentId));
        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(OrderID.of(orderId))
                .orElseThrow(() -> new PaymentBusinessRuleException(
                        "Payment not found for order: " + orderId));
        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByCustomerId(String customerId) {
        return paymentRepository.findByCustomerId(CustomerID.of(customerId))
                .stream()
                .map(PaymentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SaleResponse getSaleById(String saleId) {
        Sale sale = saleRepository.findById(SaleID.of(saleId))
                .orElseThrow(() -> new PaymentBusinessRuleException(
                        "Sale not found: " + saleId));
        return SaleResponse.from(sale);
    }

    @Transactional(readOnly = true)
    public SaleResponse getSaleByOrderId(String orderId) {
        Sale sale = saleRepository.findByOrderId(OrderID.of(orderId))
                .orElseThrow(() -> new PaymentBusinessRuleException(
                        "Sale not found for order: " + orderId));
        return SaleResponse.from(sale);
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesByCustomerId(String customerId) {
        return saleRepository.findByCustomerId(CustomerID.of(customerId))
                .stream()
                .map(SaleResponse::from)
                .toList();
    }


    /**
     * Finds a payment by its Stripe PaymentIntent ID.
     * Used when processing webhooks where we only have the Stripe ID.
     * Falls back to scanning by order since PaymentGatewayRef is stored per payment.
     */
    private Payment findPaymentByGatewayIntentId(String paymentIntentId) {
        // In a real impl this would be a repository query:
        // paymentRepository.findByGatewayPaymentIntentId(paymentIntentId)
        // For now we delegate this responsibility to the repo layer.
        return paymentRepository.findByGatewayPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new PaymentBusinessRuleException(
                        "Payment not found for Stripe PaymentIntent: " + paymentIntentId));
    }
}

