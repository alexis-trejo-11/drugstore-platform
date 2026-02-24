package io.github.alexisTrejo11.drugstore.payments.core.port.output;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Sale;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.CustomerID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.PaymentID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.SaleID;

import java.util.List;
import java.util.Optional;

public interface SaleRepository {
	boolean existsByPaymentId(PaymentID paymentId);
	Optional<Sale> findByPaymentId(PaymentID paymentId);
	Optional<Sale> findById(SaleID saleId);
	Optional<Sale> findByOrderId(OrderID orderID);
	List<Sale> findByCustomerId(CustomerID customerId);
	Sale save(Sale sale);
}
