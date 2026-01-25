package microservice.cart_service.app.cart.core.domain.model.enums;

/**
 * Represents the status of a shopping cart.
 */
public enum CartStatus {
  /**
   * Cart is active and can be modified.
   */
  ACTIVE,

  /**
   * Cart has been checked out and is being processed.
   */
  CHECKOUT_IN_PROGRESS,

  /**
   * Cart has been converted to an order.
   */
  CONVERTED_TO_ORDER,

  /**
   * Cart has been abandoned.
   */
  ABANDONED,

  /**
   * Cart has been deleted.
   */
  DELETED,

  /**
   * Cart status is unknown.
   */
  UNKNOWN;

  /**
   * Checks if the cart can be modified.
   *
   * @return true if the cart status allows modifications
   */
  public boolean isModifiable() {
    return this == ACTIVE;
  }

  /**
   * Checks if the cart is in a terminal state.
   *
   * @return true if the cart cannot transition to another state
   */
  public boolean isTerminal() {
    return this == CONVERTED_TO_ORDER || this == DELETED;
  }
}
