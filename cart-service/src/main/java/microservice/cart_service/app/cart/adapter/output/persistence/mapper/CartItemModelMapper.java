package microservice.cart_service.app.cart.adapter.output.persistence.mapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import microservice.cart_service.app.cart.adapter.output.persistence.models.AfterwardModel;
import microservice.cart_service.app.cart.core.domain.model.AfterwardsItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import microservice.cart_service.app.cart.adapter.output.persistence.models.CartItemModel;
import microservice.cart_service.app.cart.adapter.output.persistence.models.CartModel;
import microservice.cart_service.app.cart.core.domain.model.CartItem;
import microservice.cart_service.app.cart.core.domain.model.ReconstructCartItemParams;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartItemId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartTimeStamps;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ItemPrice;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.Quantity;
import microservice.cart_service.app.product.adapter.out.persistence.ProductModel;

/**
 * Mapper responsible for converting between CartItem domain models and
 * CartItemModel persistence models.
 */
@Component
public class CartItemModelMapper {

	private static final Logger log = LoggerFactory.getLogger(CartItemModelMapper.class);

	/**
	 * Maps a domain CartItem to persistence CartItemModel.
	 *
	 * @param domain the domain cart item
	 * @return the persistence model
	 */
	public CartItemModel toModel(CartItem domain) {
		if (domain == null) {
			log.warn("Attempted to map null domain CartItem to persistence");
			return null;
		}

		log.debug("Mapping domain CartItem to persistence: {}", domain.getId());

		CartItemModel persistence = new CartItemModel();

		if (domain.getId() != null) {
			persistence.setId(domain.getId().value());
		}

		if (domain.getQuantity() != null) {
			persistence.setQuantity(domain.getQuantity().value());
		}

		if (domain.getCartId() != null) {
			CartModel cartRef = new CartModel();
			cartRef.setId(domain.getCartId().value());
			persistence.setCart(cartRef);
		}

		if (domain.getProductId() != null) {
			ProductModel productRef = new ProductModel();
			productRef.setId(domain.getProductId().value());

			if (domain.getProductName() != null) {
				productRef.setName(domain.getProductName());
			}
			if (domain.getUnitPrice() != null) {
				productRef.setUnitPrice(domain.getUnitPrice().value());
			}

			persistence.setProduct(productRef);
		}

		if (domain.getTimeStamps() != null) {
			persistence.setCreatedAt(domain.getTimeStamps().getCreatedAt());
			persistence.setUpdatedAt(domain.getTimeStamps().getUpdatedAt());
		}

		return persistence;
	}

	/**
	 * Maps a persistence model CartItemModel to domain CartItem.
	 *
	 * @param model the persistence model
	 * @return the domain cart item
	 */
	public CartItem toDomain(CartItemModel model) {
		if (model == null) {
			log.warn("Attempted to map null model CartItemModel to domain");
			return null;
		}

		log.debug("Mapping model CartItemModel to domain: {}", model.getId());
		CartItemId id = model.getId() != null ? CartItemId.from(model.getId()) : CartItemId.generate();
		Quantity quantity = Quantity.of(model.getQuantity());

		CartTimeStamps timestamps = CartTimeStamps.reconstruct(
				model.getCreatedAt(),
				model.getUpdatedAt(),
				null);

		var itemParamsBuilder = ReconstructCartItemParams.builder()
				.id(id)
				.quantity(quantity)
				.timeStamps(timestamps);

		if (model.getCart() != null && model.getCart().getId() != null) {
			itemParamsBuilder = itemParamsBuilder.cartId(CartId.from(model.getCart().getId()));
		}

		if (model.getProduct() != null) {
			ProductModel product = model.getProduct();
			itemParamsBuilder = itemParamsBuilder.productId(product.getId() != null ? ProductId.from(product.getId()) : null)
					.productName(product.getName() != null ? product.getName() : "")
					.discountPerUnit(product.getDiscountPerUnit() != null ? product.getDiscountPerUnit() : BigDecimal.ZERO)
					.unitPrice(product.getUnitPrice() != null ? new ItemPrice(product.getUnitPrice()) : null);
		}

		return CartItem.reconstruct(itemParamsBuilder.build());
	}

	/**
	 * Maps a list of domain CartItems to persistence CartItemModels.
	 *
	 * @param domainList the list of domain cart items
	 * @return the list of persistence models
	 */
	public Set<CartItemModel> toModels(List<CartItem> domainList) {
		if (domainList == null) {
			log.debug("Domain cart items list is null, returning empty list");
			return Collections.emptySet();
		}

		log.debug("Mapping {} domain cart items to persistence", domainList.size());

		return domainList.stream()
				.map(this::toModel)
				.collect(java.util.stream.Collectors.toSet());
	}

	/**
	 * Maps a list of persistence CartItemModels to domain CartItems.
	 *
	 * @param models the list of persistence models
	 * @return the list of domain cart items
	 */
	public List<CartItem> toDomains(Set<CartItemModel> models) {
		if (models == null) {
			log.debug("Persistence cart items list is null, returning empty list");
			return Collections.emptyList();
		}

		log.debug("Mapping {} persistence cart items to domain", models.size());

		return models.stream()
				.map(this::toDomain)
				.toList();
	}

	public AfterwardsItem toAfterwardsDomain(AfterwardModel model) {
		log.debug("Mapping model Cart Afterwards to domain: {}", model.getId());

		CartItemId id = model.getId() != null ? CartItemId.from(model.getId()) : CartItemId.generate();

		Quantity quantity = Quantity.of(model.getQuantity());

		CartTimeStamps timestamps = CartTimeStamps.reconstruct(
				model.getCreatedAt(),
				model.getUpdatedAt(),
				null);

		var itemParamsBuilder = ReconstructCartItemParams.builder()
				.id(id)
				.quantity(quantity)
				.timeStamps(timestamps);

		if (model.getCart() != null && model.getCart().getId() != null) {
			itemParamsBuilder.cartId(CartId.from(model.getCart().getId()));
		}

		if (model.getProduct() != null) {
			ProductModel product = model.getProduct();
			itemParamsBuilder = itemParamsBuilder.productId(product.getId() != null ? ProductId.from(product.getId()) : null)
					.productName(product.getName() != null ? product.getName() : "")
					.discountPerUnit(product.getDiscountPerUnit() != null ? product.getDiscountPerUnit() : BigDecimal.ZERO)
					.unitPrice(product.getUnitPrice() != null ? new ItemPrice(product.getUnitPrice()) : null);
		}

		return AfterwardsItem.reconstruct(itemParamsBuilder.build(), model.getAddedAt());
	}

	public List<AfterwardsItem> toAfterwardsDomains(Set<AfterwardModel> models) {
		if (models == null) {
			log.debug("Persistence afterwards items list is null, returning empty list");
			return Collections.emptyList();
		}

		log.debug("Mapping {} persistence afterwards items to domain", models.size());

		return models.stream()
				.map(this::toAfterwardsDomain)
				.toList();
	}

	public AfterwardModel toAfterwardsModel(AfterwardsItem domain) {
		if (domain == null) {
			log.warn("Attempted to map null domain AfterwardsItem to persistence");
			return null;
		}

		log.debug("Mapping domain AfterwardsItem to persistence: {}", domain.getId());
		AfterwardModel persistence = new AfterwardModel();
		persistence.setId(domain.getId() != null ? domain.getId().value() : null);
		persistence.setCreatedAt(domain.getTimeStamps() != null ? domain.getTimeStamps().getCreatedAt() : null);
		persistence.setUpdatedAt(domain.getTimeStamps() != null ? domain.getTimeStamps().getUpdatedAt() : null);
		persistence.setProduct(domain.getProductId() != null ? new ProductModel(domain.getProductId().value()) : null);
		persistence.setCart(domain.getCartId() != null ? new CartModel(domain.getCartId().value()) : null);
		persistence.setAddedAt(domain.getMovedAt());

		if (domain.getQuantity() != null) {
			persistence.setQuantity(domain.getQuantity().value());
		}
		return persistence;


	}

	public Set<AfterwardModel> toAfterwardsModels(Set<AfterwardsItem> domainList) {
		if (domainList == null) {
			log.debug("Domain afterwards items list is null, returning empty list");
			return Collections.emptySet();
		}

		log.debug("Mapping {} domain afterwards items to persistence", domainList.size());

		return domainList.stream()
				.map(this::toAfterwardsModel)
				.collect(java.util.stream.Collectors.toSet());
	}
}
