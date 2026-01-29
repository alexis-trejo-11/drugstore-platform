package microservice.cart_service.app.cart.core.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import microservice.cart_service.app.cart.core.domain.events.CartPurchasedEvent;
import microservice.cart_service.app.cart.core.domain.events.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import microservice.cart_service.app.cart.core.domain.exception.CartItemNotFoundException;
import microservice.cart_service.app.cart.core.domain.exception.CartOperationException;
import microservice.cart_service.app.cart.core.domain.exception.CartValidationException;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartTimeStamps;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CustomerId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ItemPrice;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.Quantity;
import microservice.cart_service.app.cart.core.domain.validation.CartValidation;

/**
 * Cart aggregate root - represents a customer's shopping cart.
 * This is the main domain entity for cart operations with comprehensive
 * business logic and validation.
 */
public class Cart {

  private static final Logger log = LoggerFactory.getLogger(Cart.class);

  private static final int MAX_ITEMS_PER_CART = 100;

  private CartId id;
  private CustomerId customerId;
  private List<CartItem> items;
  private List<AfterwardsItem> afterwardsItems;
  private CartTimeStamps timeStamps;
  private final transient List<DomainEvent> domainEvents = new ArrayList<>();

  private Cart() {
    this.items = new ArrayList<>();
    this.afterwardsItems = new ArrayList<>();
    this.timeStamps = CartTimeStamps.now();
  }

  /**
   * Creates a new Cart for a customer with generated ID.
   *
   * @param params the parameters for creating a cart
   * @return a new Cart instance
   * @throws CartValidationException if required parameters are missing
   */
  public static Cart create(CreateCartParams params) {
    CartValidation.requireNonNull(params, "CreateCartParams");
    CartValidation.requireNonNull(params.customerId(), "Customer ID");

    Cart cart = new Cart();
    cart.id = CartId.generate();
    cart.customerId = params.customerId();

    log.info("Created new Cart: id={}, customerId={}", cart.id, cart.customerId);

    return cart;
  }

  /**
   * Reconstructs a Cart from persistence.
   *
   * @param params the parameters for reconstructing a cart
   * @return a reconstructed Cart instance
   */
  public static Cart reconstruct(ReconstructCartParams params) {
    CartValidation.requireNonNull(params, "ReconstructCartParams");
    CartValidation.requireNonNull(params.id(), "Cart ID");
    CartValidation.requireNonNull(params.customerId(), "Customer ID");

    Cart cart = new Cart();
    cart.id = params.id();
    cart.customerId = params.customerId();
    cart.items = params.items() != null ? new ArrayList<>(params.items()) : new ArrayList<>();
    cart.afterwardsItems = params.afterwardsItems() != null
        ? new ArrayList<>(params.afterwardsItems())
        : new ArrayList<>();
    
    cart.timeStamps = params.timeStamps() != null ? params.timeStamps() : CartTimeStamps.now();

    log.debug("Reconstructed Cart: id={}, itemCount={}", cart.id, cart.items.size());

    return cart;
  }

  /**
   * Adds a single item to the cart. If an item with the same product exists,
   * the quantities are merged.
   *
   * @param item the cart item to add
   * @throws CartOperationException if the cart cannot accept more items
   */
  public void addItem(CartItem item) {
    CartValidation.requireNonNull(item, "Cart item");

    log.info("Adding item to Cart: cartId={}, productId={}, quantity={}", id, item.getProductId(),
        item.getQuantityValue());

    Optional<CartItem> existingItem = findItemByProductId(item.getProductId());

    if (existingItem.isPresent()) {
      log.debug("Merging with existing item: productId={}", item.getProductId());
      existingItem.get().mergeWith(item);
    } else {
      validateCanAddItem();
      items.add(item);
      log.debug("Added new item to cart: productId={}", item.getProductId());
    }

    timeStamps.markAsUpdated();
  }

  /**
   * Adds multiple items to the cart.
   *
   * @param itemsToAdd the list of items to add
   */
  public void addItems(List<CartItem> itemsToAdd) {
    if (itemsToAdd == null || itemsToAdd.isEmpty()) {
      log.debug("No items to add to cart: cartId={}", id);
      return;
    }

    log.info("Adding {} items to Cart: cartId={}", itemsToAdd.size(), id);

    for (CartItem item : itemsToAdd) {
      addItem(item);
    }
  }

  /**
   * Updates the quantity of an item in the cart.
   *
   * @param productId   the product ID of the item to update
   * @param newQuantity the new quantity
   * @throws CartItemNotFoundException if the item is not found
   */
  public void updateItemQuantity(ProductId productId, Quantity newQuantity) {
    CartValidation.requireNonNull(productId, "Product ID");
    CartValidation.requireNonNull(newQuantity, "New quantity");

    log.info("Updating item quantity: cartId={}, productId={}, newQuantity={}", id, productId, newQuantity.value());

    CartItem item = findItemByProductIdOrThrow(productId);
    item.updateQuantity(newQuantity);
    timeStamps.markAsUpdated();
  }

  /**
   * Updates the quantity of an item in the cart using an integer value.
   *
   * @param productId   the product ID of the item to update
   * @param newQuantity the new quantity value
   * @throws CartItemNotFoundException if the item is not found
   */
  public void updateItemQuantity(ProductId productId, int newQuantity) {
    updateItemQuantity(productId, Quantity.of(newQuantity));
  }

  /**
   * Removes a single item from the cart by product ID.
   *
   * @param productId the product ID of the item to remove
   * @return true if an item was removed
   */
  public boolean removeItem(ProductId productId) {
    CartValidation.requireNonNull(productId, "Product ID");

    log.info("Removing item from Cart: cartId={}, productId={}", id, productId);

    boolean removed = items.removeIf(item -> item.getProductId().equals(productId));

    if (removed) {
      timeStamps.markAsUpdated();
      log.debug("Item removed successfully: productId={}", productId);
    } else {
      log.warn("Item not found for removal: productId={}", productId);
    }

    return removed;
  }

  /**
   * Removes multiple items from the cart by product IDs.
   *
   * @param productIds the list of product IDs to remove
   * @return the count of items removed
   */
  public int removeItems(List<ProductId> productIds) {
    if (productIds == null || productIds.isEmpty()) {
      log.debug("No items to remove from cart: cartId={}", id);
      return 0;
    }

    log.info("Removing {} items from Cart: cartId={}", productIds.size(), id);

    int initialSize = items.size();
    items.removeIf(item -> productIds.contains(item.getProductId()));
    int removedCount = initialSize - items.size();

    if (removedCount > 0) {
      timeStamps.markAsUpdated();
    }
    log.debug("Removed {} items from cart", removedCount);
    return removedCount;
  }

  /**
   * Clears all items from the cart.
   */
  public void clear() {
    log.info("Clearing Cart: cartId={}, itemCount={}", id, items.size());

    items.clear();
    timeStamps.markAsUpdated();

    log.debug("Cart cleared successfully");
  }

  /**
   * Sets the items in the cart, replacing any existing items.
   *
   * @param newItems the new list of items
   */
  public void setItems(List<CartItem> newItems) {
    log.info("Setting items in Cart: cartId={}, newItemCount={}", id, newItems != null ? newItems.size() : 0);
    if (newItems == null) {
      throw new CartValidationException("New items list cannot be null");
    }
    if (newItems.size() > MAX_ITEMS_PER_CART) {
      throw new CartOperationException("setItems",
          String.format("Cart cannot have more than %d unique items", MAX_ITEMS_PER_CART));
    }

    this.items = new ArrayList<>(newItems);
    timeStamps.markAsUpdated();
  }

  /**
   * Finds an item in the cart by product ID.
   *
   * @param productId the product ID to search for
   * @return Optional containing the item if found
   */
  public Optional<CartItem> findItemByProductId(ProductId productId) {
    if (productId == null) {
      return Optional.empty();
    }
    return items.stream().filter(item -> item.getProductId().equals(productId)).findFirst();
  }

  /**
   * Finds an item in the cart by product ID or throws an exception.
   *
   * @param productId the product ID to search for
   * @return the cart item
   * @throws CartItemNotFoundException if the item is not found
   */
  public CartItem findItemByProductIdOrThrow(ProductId productId) {
    return findItemByProductId(productId).orElseThrow(() -> new CartItemNotFoundException(productId));
  }

  /**
   * Checks if the cart contains an item with the specified product ID.
   *
   * @param productId the product ID to check
   * @return true if the cart contains the product
   */
  public boolean containsProduct(ProductId productId) {
    return findItemByProductId(productId).isPresent();
  }

  /**
   * Gets the total number of individual items in the cart.
   *
   * @return the sum of all item quantities
   */
  public int getTotalItemCount() {
    return items.stream().mapToInt(CartItem::getQuantityValue).sum();
  }

  /**
   * Gets the number of unique products in the cart.
   *
   * @return the count of unique products
   */
  public int getUniqueProductCount() {
    return items.size();
  }

  /**
   * Calculates the total price of all items in the cart.
   *
   * @return the total price as ItemPrice
   */
  public ItemPrice calculateTotal() {
    if (items.isEmpty()) {
      return ItemPrice.zero();
    }

    return items.stream()
        .map(CartItem::calculateTotal)
        .reduce(ItemPrice.zero(), ItemPrice::add);
  }

  public ItemPrice calculateSubtotal() {
    if (items.isEmpty()) {
      return ItemPrice.zero();
    }

    return items.stream()
        .map(CartItem::calculateSubtotal)
        .reduce(ItemPrice.zero(), ItemPrice::add);
  }

  /**
   * Gets all product IDs in the cart.
   *
   * @return list of product IDs
   */
  public List<ProductId> getProductIds() {
    return items.stream().map(CartItem::getProductId).toList();
  }

  /**
   * Checks if the cart is empty.
   *
   * @return true if the cart has no items
   */
  public boolean isEmpty() {
    return items.isEmpty();
  }

  /**
   * Checks if the cart has reached its maximum capacity.
   *
   * @return true if the cart is at maximum capacity
   */
  public boolean isFull() {
    return items.size() >= MAX_ITEMS_PER_CART;
  }

  private void validateCanAddItem() {
    if (isFull()) {
      throw new CartOperationException("addItem",
          String.format("Cart cannot have more than %d unique item", MAX_ITEMS_PER_CART));
    }
  }

  /**
   * Checks if the cart has been soft-deleted.
   *
   * @return true if the cart is deleted
   */
  public boolean isDeleted() {
    return timeStamps != null && timeStamps.isDeleted();
  }

  /**
   * Soft deletes the cart.
   */
  public void softDelete() {
    log.info("Soft deleting Cart: cartId={}", id);
    timeStamps.markAsDeleted();
    timeStamps.markAsUpdated();
  }

  /**
   * Restores a soft-deleted cart.
   */
  public void restore() {
    log.info("Restoring Cart: cartId={}", id);
    timeStamps.restore();
    timeStamps.markAsUpdated();
  }

  /**
   * Moves specified items from the cart to afterwards.
   *
   * @param productIds the list of product IDs to move
   */
  public void moveItemsToAfterwards(List<ProductId> productIds) {
    if (productIds == null || productIds.isEmpty()) {
      log.debug("No items to move to afterwards: cartId={}", id);
      return;
    }
    List<CartItem> itemsToMove = items.stream()
        .filter(item -> productIds.contains(item.getProductId()))
        .toList();

    List<AfterwardsItem> afterwardsItemsToAdd = itemsToMove.stream()
        .map(AfterwardsItem::createFromItem)
        .toList();

    log.info("Moving {} items to afterwards: cartId={}", afterwardsItemsToAdd.size(), id);
    this.afterwardsItems.addAll(afterwardsItemsToAdd);
    this.items.removeAll(itemsToMove);

    log.info("Moved items to afterwards successfully: cartId={}, movedItemCount={}", id, afterwardsItemsToAdd.size());
    timeStamps.markAsUpdated();
  }

  /**
   * Returns specified items from afterwards back to the cart.
   *
   * @param productIds the list of product IDs to return
   */
  public void returnItemsFromAfterwards(List<ProductId> productIds) {
    if (productIds == null || productIds.isEmpty()) {
      log.debug("No items to return from afterwards: cartId={}", id);
      return;
    }
    List<AfterwardsItem> itemsToReturn = afterwardsItems.stream()
        .filter(item -> productIds.contains(item.getProductId()))
        .toList();

    List<CartItem> returnedItems = itemsToReturn.stream()
        .map(item -> CartItem.reconstruct(
            new ReconstructCartItemParams(
                item.getId(),
                item.getCartId(),
                item.getProductId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getDiscountPerUnit(),
                item.getTimeStamps())))
        .toList();

    this.items.addAll(returnedItems);
    this.afterwardsItems.removeAll(itemsToReturn);

    timeStamps.markAsUpdated();
  }

  public  List<CartItem> getItemsExcludingProducts(List<ProductId> productIdsToExclude) {
    if (productIdsToExclude == null || productIdsToExclude.isEmpty()) {
      return new ArrayList<>(this.items);
    }
    List<CartItem> itemsToKeep = items.stream()
        .filter(item -> !productIdsToExclude.contains(item.getProductId()))
        .toList();

    return new ArrayList<>(itemsToKeep);
  }

  public void purchaseItems(List<ProductId> productIdsToExclude) {
    if (productIdsToExclude == null) {
      productIdsToExclude = List.of();
    }

    log.info("Processing purchase for Cart: cartId={}, itemsToExclude={}",
        id, productIdsToExclude.size());

    List<ProductId> finalProductIdsToExclude = productIdsToExclude;
    List<ProductId> purchasedProductIds = items.stream()
        .map(CartItem::getProductId)
        .filter(productId -> !finalProductIdsToExclude.contains(productId))
        .toList();

    if (productIdsToExclude.isEmpty()) {
      this.items.clear();
    } else {
      List<ProductId> finalProductIdsToExclude1 = productIdsToExclude;
      List<CartItem> itemsToKeep = items.stream()
          .filter(item -> finalProductIdsToExclude1.contains(item.getProductId()))
          .toList();
      this.items = new ArrayList<>(itemsToKeep);
    }

    if (!purchasedProductIds.isEmpty()) {
      DomainEvent event = new CartPurchasedEvent(
          this.id,
          this.customerId,
          purchasedProductIds,
          productIdsToExclude
      );
      domainEvents.add(event);
      log.info("Cart purchase event recorded: event={}", event);
    } else {
      log.info("No products purchased, skipping event creation");
    }

    timeStamps.markAsUpdated();
  }

  public BigDecimal calculateDiscount() {
    if (items.isEmpty()) {
      return BigDecimal.ZERO;
    }


    return items.stream()
        .map(CartItem::calculateDiscount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public void clearDomainEvents() {
    domainEvents.clear();
  }

  public List<DomainEvent> getDomainEvents() {
    return List.copyOf(domainEvents);
  }

  public CartId getId() {
    return id;
  }

  public CustomerId getCustomerId() {
    return customerId;
  }

  public List<CartItem> getItems() {
    return Collections.unmodifiableList(items);
  }

  public CartTimeStamps getTimeStamps() {
    return timeStamps;
  }

  public List<AfterwardsItem> getAfterwardsItems() {
    return Collections.unmodifiableList(this.afterwardsItems);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Cart cart = (Cart) o;
    return Objects.equals(id, cart.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return String.format("Cart{id=%s, customerId=%s, itemCount=%d, totalItems=%d}", id, customerId,
        getUniqueProductCount(), getTotalItemCount());
  }
}
