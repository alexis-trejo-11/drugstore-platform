package microservice.cart_service.app.cart.core.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import microservice.cart_service.app.cart.core.domain.exception.CartValidationException;
import microservice.cart_service.app.cart.core.domain.exception.InvalidQuantityException;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartItemId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.CartTimeStamps;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ItemPrice;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.ProductId;
import microservice.cart_service.app.cart.core.domain.model.valueobjects.Quantity;
import microservice.cart_service.app.cart.core.domain.validation.CartValidation;

/**
 * CartItem entity representing a product in a shopping cart.
 * This is a domain entity with business logic for managing cart item
 * operations.
 */
public class CartItem {

  private static final Logger log = LoggerFactory.getLogger(CartItem.class);

  protected CartItemId id;
  protected CartId cartId;
  protected ProductId productId;
  protected String productName;
  protected ItemPrice unitPrice;
  protected Quantity quantity;
  protected BigDecimal discountPerUnit;
  protected CartTimeStamps timeStamps;

  public CartItem() {
    this.timeStamps = CartTimeStamps.now();
  }

  /**
   * Creates a new CartItem with generated ID.
   *
   * @param params the parameters for creating a cart item
   * @return a new CartItem instance
   * @throws CartValidationException if required parameters are missing
   */
  public static CartItem create(CreateCartItemParams params) {
    CartValidation.requireNonNull(params, "CreateCartItemParams");
    CartValidation.requireNonNull(params.cartId(), "Cart ID");
    CartValidation.requireNonNull(params.productId(), "Product ID");
    CartValidation.requireNonNull(params.quantity(), "Quantity");

    if (params.quantity().value() <= 0) {
      throw new InvalidQuantityException(params.quantity().value(),
          "Quantity must be greater than zero when creating a cart item");
    }

    CartItem item = new CartItem();
    item.id = CartItemId.generate();
    item.cartId = params.cartId();
    item.productId = params.productId();
    item.quantity = params.quantity();

    log.info("Created new CartItem: id={}, productId={}, quantity={}",
        item.id, item.productId, item.quantity.value());

    return item;
  }

  /**
   * Reconstructs a CartItem from persistence.
   *
   * @param params the parameters for reconstructing a cart item
   * @return a reconstructed CartItem instance
   */
  public static CartItem reconstruct(ReconstructCartItemParams params) {
    CartValidation.requireNonNull(params, "ReconstructCartItemParams");
    CartValidation.requireNonNull(params.id(), "CartItem ID");

    CartItem item = new CartItem();
    item.id = params.id();
    item.cartId = params.cartId();
    item.productId = params.productId();
    item.productName = params.productName() != null ? params.productName() : "";
    item.unitPrice = params.unitPrice();
    item.quantity = params.quantity();
    item.discountPerUnit = params.discountPerUnit() != null ? params.discountPerUnit() : BigDecimal.ZERO;
    item.timeStamps = params.timeStamps() != null ? params.timeStamps() : CartTimeStamps.now();

    log.debug("Reconstructed CartItem: id={}", item.id);

    return item;
  }

  /**
   * Updates the quantity of this cart item.
   *
   * @param newQuantity the new quantity
   * @throws InvalidQuantityException if the new quantity is invalid
   */
  public void updateQuantity(Quantity newQuantity) {
    CartValidation.requireNonNull(newQuantity, "New quantity");

    log.info("Updating CartItem quantity: id={}, oldQuantity={}, newQuantity={}",
        id, quantity.value(), newQuantity.value());

    this.quantity = newQuantity;
    this.timeStamps.markAsUpdated();
  }

  /**
   * Updates the quantity of this cart item using an integer value.
   *
   * @param newQuantity the new quantity value
   * @throws InvalidQuantityException if the new quantity is invalid
   */
  public void updateQuantity(int newQuantity) {
    updateQuantity(Quantity.of(newQuantity));
  }

  /**
   * Increases the quantity by the specified amount.
   *
   * @param amount the amount to add
   * @throws InvalidQuantityException if the resulting quantity would exceed
   *                                  maximum
   */
  public void increaseQuantity(int amount) {
    if (amount <= 0) {
      throw new InvalidQuantityException(amount, "Increase amount must be positive");
    }

    log.info("Increasing CartItem quantity: id={}, currentQuantity={}, increaseBy={}",
        id, quantity.value(), amount);

    this.quantity = this.quantity.increase(amount);
    this.timeStamps.markAsUpdated();
  }

  /**
   * Decreases the quantity by the specified amount.
   *
   * @param amount the amount to subtract
   * @throws InvalidQuantityException if the resulting quantity would be less than
   *                                  minimum
   */
  public void decreaseQuantity(int amount) {
    if (amount <= 0) {
      throw new InvalidQuantityException(amount, "Decrease amount must be positive");
    }

    log.info("Decreasing CartItem quantity: id={}, currentQuantity={}, decreaseBy={}",
        id, quantity.value(), amount);

    this.quantity = this.quantity.decrease(amount);
    this.timeStamps.markAsUpdated();
  }

  /**
   * Updates the unit price of this cart item.
   *
   * @param newPrice the new unit price
   */
  public void updatePrice(ItemPrice newPrice) {
    CartValidation.requireNonNull(newPrice, "New price");

    log.info("Updating CartItem price: id={}, oldPrice={}, newPrice={}",
        id, unitPrice, newPrice);

    this.unitPrice = newPrice;
    this.timeStamps.markAsUpdated();
  }

  /**
   * Calculates the subtotal for this cart item (unitPrice * quantity).
   *
   * @return the subtotal as ItemPrice
   */
  public ItemPrice calculateSubtotal() {
    if (unitPrice == null) {
      return ItemPrice.NONE;
    }
    return unitPrice.multiply(quantity);
  }

  public BigDecimal calculateDiscount() {
    if (discountPerUnit == null) {
      return BigDecimal.ZERO;
    }
    return discountPerUnit.multiply(BigDecimal.valueOf(quantity.value()));
  }

  public ItemPrice calculateTotal() {
    ItemPrice subtotal = calculateSubtotal();
    BigDecimal totalDiscount = calculateDiscount();
    return subtotal.subtract(new ItemPrice(totalDiscount));
  }

  /**
   * Checks if this cart item can be merged with another item.
   * Items can be merged if they have the same product ID.
   *
   * @param other the other cart item
   * @return true if items can be merged
   */
  public boolean canMergeWith(CartItem other) {
    return this.productId.equals(other.productId);
  }

  /**
   * Merges another cart item's quantity into this one.
   *
   * @param other the cart item to merge
   * @throws CartValidationException if items cannot be merged
   */
  public void mergeWith(CartItem other) {
    if (!canMergeWith(other)) {
      throw new CartValidationException("Cannot merge cart items with different products");
    }

    log.info("Merging CartItem: id={}, merging quantity {} from item {}",
        id, other.quantity.value(), other.id);

    increaseQuantity(other.quantity.value());
  }

  /**
   * Checks if this item is deleted.
   *
   * @return true if the item has been soft-deleted
   */
  public boolean isDeleted() {
    return timeStamps != null && timeStamps.isDeleted();
  }

  /**
   * Soft deletes this cart item.
   */
  public void softDelete() {
    log.info("Soft deleting CartItem: id={}", id);
    this.timeStamps.markAsDeleted();
    this.timeStamps.markAsUpdated();
  }

  public CartItemId getId() {
    return id;
  }

  public CartId getCartId() {
    return cartId;
  }

  public ProductId getProductId() {
    return productId;
  }

  public String getProductName() {
    return productName;
  }

  public ItemPrice getUnitPrice() {
    return unitPrice;
  }

  public BigDecimal getDiscountPerUnit() {
    return discountPerUnit;
  }

  public Quantity getQuantity() {
    return quantity;
  }

  public int getQuantityValue() {
    return quantity.value();
  }

  public CartTimeStamps getTimeStamps() {
    return timeStamps;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    CartItem cartItem = (CartItem) o;
    // Cart items are equal if they have the same product ID (within the same cart)
    return Objects.equals(productId, cartItem.productId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productId);
  }

  @Override
  public String toString() {
    return String.format("CartItem{id=%s, cartId=%s, productId=%s, productName='%s', quantity=%d, unitPrice=%s}",
        id, cartId, productId, productName, quantity.value(), unitPrice);
  }
}
