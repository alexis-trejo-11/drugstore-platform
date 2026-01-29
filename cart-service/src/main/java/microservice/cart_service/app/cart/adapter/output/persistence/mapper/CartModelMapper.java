package microservice.cart_service.app.cart.adapter.output.persistence.mapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import microservice.cart_service.app.cart.adapter.output.persistence.models.AfterwardModel;
import microservice.cart_service.app.cart.core.domain.model.AfterwardsItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import microservice.cart_service.app.cart.adapter.output.persistence.models.CartItemModel;
import microservice.cart_service.app.cart.adapter.output.persistence.models.CartModel;
import microservice.cart_service.app.cart.core.domain.model.Cart;
import microservice.cart_service.app.cart.core.domain.model.CartItem;
import microservice.cart_service.app.cart.core.domain.model.ReconstructCartParams;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartTimeStamps;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;

/**
 * Mapper responsible for converting between Cart domain models and
 * CartModel persistence models.
 */
@Component
public class CartModelMapper {
	private static final Logger log = LoggerFactory.getLogger(CartModelMapper.class);
	private final CartItemModelMapper itemMapper;

	@Autowired
	public CartModelMapper(CartItemModelMapper itemMapper) {
		this.itemMapper = itemMapper;
	}

	/**
	 * Maps a domain Cart to persistence CartModel.
	 *
	 * @param domain the domain cart
	 * @return the persistence model
	 */
	public CartModel toModel(Cart domain) {
		if (domain == null) {
			log.warn("Attempted to map null domain Cart to model");
			return null;
		}

		log.debug("Mapping domain Cart to model: {}", domain.getId());
		CartModel itemModel = new CartModel();

		if (domain.getId() != null) {
			itemModel.setId(domain.getId().value());
		}

		if (domain.getCustomerId() != null) {
			itemModel.setCustomerId(domain.getCustomerId().value());
		}

		if (domain.getTimeStamps() != null) {
			itemModel.setCreatedAt(domain.getTimeStamps().getCreatedAt());
			itemModel.setUpdatedAt(domain.getTimeStamps().getUpdatedAt());
		}

		Set<CartItemModel> items = itemMapper.toModels(domain.getItems());
		Set<AfterwardModel> afterwardsItems = itemMapper.toAfterwardsModels(new HashSet<>(domain.getAfterwardsItems()));
		itemModel.setAfterwardItems(afterwardsItems);
		itemModel.setCartItems(items);

		return itemModel;
	}

	/**
	 * Maps a persistence CartModel to domain Cart.
	 *
	 * @param model the persistence model
	 * @return the domain cart
	 */
	public Cart toDomain(CartModel model, boolean includeItems, boolean includeAfterwards) {
		if (model == null) {
			log.warn("Attempted to map null model CartModel to domain");
			return null;
		}

		log.debug("Mapping model CartModel to domain: {}", model.getId());

		List<CartItem> items = includeItems
				? itemMapper.toDomains(model.getCartItems())
				: Collections.emptyList();

		List<AfterwardsItem> afterwardsItems = includeAfterwards
				? itemMapper.toAfterwardsDomains(model.getAfterwardItems())
				: Collections.emptyList();

		CustomerId customerId = model.getCustomerId() != null ? CustomerId.from(model.getCustomerId()) : null;
		CartTimeStamps timestamps = CartTimeStamps.reconstruct(model.getCreatedAt(), model.getUpdatedAt(), null);

		ReconstructCartParams params = new ReconstructCartParams(
				CartId.from(model.getId()),
				customerId,
				items,
				afterwardsItems,
				timestamps);

		return Cart.reconstruct(params);
	}

	/**
	 * Maps a list of domain Carts to model CartModels.
	 *
	 * @param domains the list of domain carts
	 * @return the list of model carts
	 */
	public List<CartModel> toModels(List<Cart> domains) {
		if (domains == null) {
			log.debug("Domain carts list is null, returning empty list");
			return Collections.emptyList();
		}

		log.debug("Mapping {} domain carts to model", domains.size());

		return domains.stream()
				.map(this::toModel)
				.toList();
	}

	/**
	 * Maps a list of persistence CartModels to domain Carts.
	 *
	 * @param models the list of persistence models
	 * @return the list of domain carts
	 */
	public List<Cart> toDomains(List<CartModel> models, boolean includeItems, boolean includeAfterwards) {
		if (models == null) {
			log.debug("Persistence carts list is null, returning empty list");
			return Collections.emptyList();
		}

		log.debug("Mapping {} persistence carts to domain", models.size());

		return models.stream()
				.map(model -> toDomain(model, includeItems, includeAfterwards))
				.toList();
	}
}
