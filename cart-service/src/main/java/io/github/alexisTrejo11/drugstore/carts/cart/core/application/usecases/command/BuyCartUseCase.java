package io.github.alexisTrejo11.drugstore.carts.cart.core.application.usecases.command;

import io.github.alexisTrejo11.drugstore.carts.cart.core.application.command.BuyCartCommand;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartNotFoundException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartValidationException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.Cart;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.CartItem;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.out.CartRepository;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.out.DomainEventPublisher;
import io.github.alexisTrejo11.drugstore.carts.product.core.port.in.ProductUseCases;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class BuyCartUseCase {
	private final CartRepository cartRepository;
	private final ProductUseCases productUseCases;
	private final DomainEventPublisher eventPublisher;

	@Autowired
	public BuyCartUseCase(
			CartRepository cartRepository,
			ProductUseCases productUseCases,
			DomainEventPublisher eventPublisher) {
		this.cartRepository = cartRepository;
		this.productUseCases = productUseCases;
		this.eventPublisher = eventPublisher;
	}

	@Transactional
	public void execute(BuyCartCommand command) {
		Cart cart = cartRepository.findByCustomerIdWithItems(command.customerId())
				.orElseThrow(() -> new CartNotFoundException(command.customerId()));

		/*
		List<ProductId> productsToPurchase = validateProductsAvailability(
				cart,
				command.productsToExclude()
		);
		 */

		cart.purchaseItems(command.productsToExclude());
		cartRepository.save(cart);

    publishDomainEvents(cart);
	}


	private List<ProductId> validateProductsAvailability(Cart cart, List<ProductId> productsToExclude) {
		List<ProductId> productsToPurchase = cart.getItems().stream()
				.map(CartItem::getProductId)
				.filter(productId -> productsToExclude == null ||
						!productsToExclude.contains(productId))
				.toList();

		if (productsToPurchase.isEmpty()) {
			return List.of();
		}

		try {
			CompletableFuture<List<ProductId>> futureStock = CompletableFuture.supplyAsync(() ->
					productUseCases.getOutOfStockProductsIn(productsToPurchase)
			);

			CompletableFuture<List<ProductId>> futureUnavailable = CompletableFuture.supplyAsync(() ->
					productUseCases.getUnavailableProductsIn(productsToPurchase)
			);

			// Esperar con timeout
			List<ProductId> outOfStockProducts = futureStock.get(5, TimeUnit.SECONDS);
			List<ProductId> unavailableProducts = futureUnavailable.get(5, TimeUnit.SECONDS);

			// Validar resultados
			if (!outOfStockProducts.isEmpty()) {
				throw new CartValidationException(
						"Products out of stock: " + outOfStockProducts
				);
			}

			if (!unavailableProducts.isEmpty()) {
				throw new CartValidationException(
						"Products unavailable: " + unavailableProducts
				);
			}

			return productsToPurchase;

		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new CartValidationException("Error validating product availability", e);
		}
	}

	private void publishDomainEvents(Cart cart) {
		cart.getDomainEvents().forEach(eventPublisher::publish);
		cart.clearDomainEvents();
	}
}
