package io.github.alexisTrejo11.drugstore.payments.core.port.input;

import io.github.alexisTrejo11.drugstore.payments.core.application.dto.request.InitiatePaymentRequest;
import io.github.alexisTrejo11.drugstore.payments.core.application.dto.request.RefundRequest;
import io.github.alexisTrejo11.drugstore.payments.core.application.dto.request.StripeWebhookRequest;
import io.github.alexisTrejo11.drugstore.payments.core.application.dto.response.PaymentResponse;
import io.github.alexisTrejo11.drugstore.payments.core.application.dto.response.SaleResponse;

import java.util.List;

/**
 * Payment Application Service Interface.
 * 
 * Defines the contract for orchestrating payment and sale workflows.
 * Coordinates domain services, repositories and external ports (Stripe).
 */
public interface PaymentApplicationService {

    /**
     * Initiates a payment flow by creating a Payment aggregate and Stripe PaymentIntent.
     *
     * @param request payment initiation details
     * @return payment response with client secret for frontend confirmation
     */
    PaymentResponse initiatePayment(InitiatePaymentRequest request);

    /**
     * Processes Stripe webhook events for payment state transitions.
     *
     * @param webhook verified Stripe webhook event data
     */
    void handleStripeWebhook(StripeWebhookRequest webhook);

    /**
     * Processes a refund (full or partial) through Stripe and updates domain state.
     *
     * @param request refund details
     * @return updated payment response
     */
    PaymentResponse processRefund(RefundRequest request);

    /**
     * Retrieves a payment by its ID.
     *
     * @param paymentId payment identifier
     * @return payment details
     */
    PaymentResponse getPaymentById(String paymentId);

    /**
     * Retrieves a payment by its associated order ID.
     *
     * @param orderId order identifier
     * @return payment details
     */
    PaymentResponse getPaymentByOrderId(String orderId);

    /**
     * Retrieves all payments for a specific customer.
     *
     * @param customerId customer identifier
     * @return list of payment details
     */
    List<PaymentResponse> getPaymentsByCustomerId(String customerId);

    /**
     * Retrieves a sale by its ID.
     *
     * @param saleId sale identifier
     * @return sale details
     */
    SaleResponse getSaleById(String saleId);

    /**
     * Retrieves a sale by its associated order ID.
     *
     * @param orderId order identifier
     * @return sale details
     */
    SaleResponse getSaleByOrderId(String orderId);

    /**
     * Retrieves all sales for a specific customer.
     *
     * @param customerId customer identifier
     * @return list of sale details
     */
    List<SaleResponse> getSalesByCustomerId(String customerId);
}
