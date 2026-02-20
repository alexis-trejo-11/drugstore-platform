package io.github.alexisTrejo11.drugstore.carts.cart.core.application.command;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.exception.CartValidationException;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.CartItem;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.CreateCartItemParams;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.Quantity;
import io.github.alexisTrejo11.drugstore.carts.product.core.domain.Product;

/**
 * Factory class responsible for creating CartItems from Products and
 * quantities.
 * Separates the complex cart item creation logic from the command record.
 */
public class CartItemFactory {

  /**
   * Creates multiple cart items from a collection of products and their
   * quantities.
   *
   * @param products             the collection of products to create cart items
   *                             for
   * @param productQuantitiesMap map of product IDs to their quantities
   * @return set of created cart items
   * @throws CartValidationException if a product is not found in the quantities
   *                                  map
   */
  public static Set<CartItem> createItems(Set<Product> products, Map<ProductId, Integer> productQuantitiesMap) {
    return products.stream()
        .map(product -> createItemForProduct(product, productQuantitiesMap))
        .collect(Collectors.toSet());
  }

  /**
   * Creates a single cart item for a specific product.
   *
   * @param product              the product to create a cart item for
   * @param productQuantitiesMap map of product IDs to their quantities
   * @return the created cart item
   * @throws CartValidationException if the product is not found in the
   *                                  quantities map
   */
  public static CartItem createItemForProduct(Product product, Map<ProductId, Integer> productQuantitiesMap) {
    Integer quantity = productQuantitiesMap.get(product.getId());
    if (quantity == null) {
      throw new CartValidationException("Product with ID " + product.getId() + " not found in cart update request");
    }

    var params = createCartItemParams(product, quantity);
    return CartItem.create(params);
  }

  /**
   * Creates a map of ProductId to CartItem for easier lookup operations.
   *
   * @param items the cart items to map
   * @return map of product ID to cart item
   */
  public static Map<ProductId, CartItem> createItemsMap(Set<CartItem> items) {
    return items.stream()
        .collect(Collectors.toMap(CartItem::getProductId, item -> item));
  }

  private static CreateCartItemParams createCartItemParams(Product product, Integer quantity) {
    return CreateCartItemParams.builder()
        .productId(product.getId())
        .quantity(Quantity.of(quantity))
        .build();
  }
}
