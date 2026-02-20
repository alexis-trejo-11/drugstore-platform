package io.github.alexisTrejo11.drugstore.carts.cart.adapter.output.persistence.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.output.persistence.models.AfterwardModel;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.output.persistence.models.CartItemModel;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.output.persistence.models.CartModel;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.specficication.CartSearchCriteria;
import io.github.alexisTrejo11.drugstore.carts.product.adapter.output.persistence.ProductModel;

@Component
public class CartSpecificationBuilder {

  public Specification<CartModel> build(CartSearchCriteria criteria) {
    if (criteria == null) {
      return Specification.where(null);
    }

    return Specification.where(buildCartSpecifications(criteria))
        .and(buildCartItemSpecifications(criteria))
        .and(buildAfterwardItemSpecifications(criteria))
        .and(buildProductSpecifications(criteria));
  }

  private Specification<CartModel> buildCartSpecifications(CartSearchCriteria criteria) {
    return Specification.where(cartIdEquals(criteria.cartId()))
        .and(customerIdEquals(criteria.customerId()))
        .and(createdAfter(criteria.createdAfter()))
        .and(createdBefore(criteria.createdBefore()))
        .and(updatedAfter(criteria.updatedAfter()))
        .and(updatedBefore(criteria.updatedBefore()))
        .and(hasItems(criteria.hasItems()))
        .and(totalItemsRange(criteria.minTotalItems(), criteria.maxTotalItems()))
        .and(hasAfterwardItems(criteria.hasAfterwardItems()));
  }

  private Specification<CartModel> buildCartItemSpecifications(CartSearchCriteria criteria) {
    return Specification.where(productIdsIn(criteria.productIds()))
        .and(productNameEquals(criteria.productName()))
        .and(productNameContains(criteria.productNameContains()))
        .and(itemQuantityRange(criteria.minItemQuantity(), criteria.maxItemQuantity()))
        .and(itemPriceRange(criteria.minItemPrice(), criteria.maxItemPrice()))
        .and(discountRange(criteria.minDiscountPerUnit(), criteria.maxDiscountPerUnit()));
  }

  private Specification<CartModel> buildAfterwardItemSpecifications(CartSearchCriteria criteria) {
    return Specification.where(afterwardProductIdsIn(criteria.afterwardProductIds()))
        .and(afterwardQuantityRange(criteria.minAfterwardQuantity(), criteria.maxAfterwardQuantity()))
        .and(afterwardAddedAfter(criteria.afterwardAddedAfter()))
        .and(afterwardAddedBefore(criteria.afterwardAddedBefore()));
  }

  private Specification<CartModel> buildProductSpecifications(CartSearchCriteria criteria) {
    return Specification.where(productAvailable(criteria.productAvailable()))
        .and(productPriceRange(criteria.minProductPrice(), criteria.maxProductPrice()));
  }

  // Cart specifications
  private Specification<CartModel> cartIdEquals(String cartId) {
    return (root, query, cb) -> cartId != null ? cb.equal(root.get("id"), cartId) : null;
  }

  private Specification<CartModel> customerIdEquals(String customerId) {
    return (root, query, cb) -> customerId != null ? cb.equal(root.get("customerId"), customerId) : null;
  }

  private Specification<CartModel> createdAfter(java.time.LocalDateTime createdAfter) {
    return (root, query, cb) -> createdAfter != null ? cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter)
        : null;
  }

  private Specification<CartModel> createdBefore(java.time.LocalDateTime createdBefore) {
    return (root, query, cb) -> createdBefore != null ? cb.lessThanOrEqualTo(root.get("createdAt"), createdBefore)
        : null;
  }

  private Specification<CartModel> updatedAfter(java.time.LocalDateTime updatedAfter) {
    return (root, query, cb) -> updatedAfter != null ? cb.greaterThanOrEqualTo(root.get("updatedAt"), updatedAfter)
        : null;
  }

  private Specification<CartModel> updatedBefore(java.time.LocalDateTime updatedBefore) {
    return (root, query, cb) -> updatedBefore != null ? cb.lessThanOrEqualTo(root.get("updatedAt"), updatedBefore)
        : null;
  }

  private Specification<CartModel> hasItems(Boolean hasItems) {
    return (root, query, cb) -> {
      if (hasItems == null)
        return null;

      Join<CartModel, CartItemModel> itemsJoin = root.join("cartItems", JoinType.LEFT);
      if (hasItems) {
        return cb.isNotNull(itemsJoin.get("id"));
      } else {
        return cb.isNull(itemsJoin.get("id"));
      }
    };
  }

  private Specification<CartModel> totalItemsRange(Integer minTotalItems, Integer maxTotalItems) {
    return (root, query, cb) -> {
      if (minTotalItems == null && maxTotalItems == null)
        return null;

      Subquery<Long> subquery = query.subquery(Long.class);
      Root<CartModel> subRoot = subquery.from(CartModel.class);
      Join<CartModel, CartItemModel> itemsJoin = subRoot.join("cartItems", JoinType.LEFT);
      subquery.select(cb.count(itemsJoin.get("id")))
          .where(cb.equal(subRoot.get("id"), root.get("id")));

      Expression<Long> itemCount = subquery.getSelection();

      List<Predicate> predicates = new ArrayList<>();
      if (minTotalItems != null) {
        predicates.add(cb.ge(itemCount, minTotalItems.longValue()));
      }
      if (maxTotalItems != null) {
        predicates.add(cb.le(itemCount, maxTotalItems.longValue()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private Specification<CartModel> hasAfterwardItems(Boolean hasAfterwardItems) {
    return (root, query, cb) -> {
      if (hasAfterwardItems == null)
        return null;

      Join<CartModel, AfterwardModel> afterwardJoin = root.join("afterwardItems", JoinType.LEFT);
      if (hasAfterwardItems) {
        return cb.isNotNull(afterwardJoin.get("id"));
      } else {
        return cb.isNull(afterwardJoin.get("id"));
      }
    };
  }

  // Cart item specifications
  private Specification<CartModel> productIdsIn(List<String> productIds) {
    return (root, query, cb) -> {
      if (productIds == null || productIds.isEmpty())
        return null;

      Join<CartModel, CartItemModel> itemsJoin = root.join("cartItems", JoinType.INNER);
      Join<CartItemModel, ProductModel> productJoin = itemsJoin.join("product", JoinType.INNER);
      return productJoin.get("id").in(productIds);
    };
  }

  private Specification<CartModel> productNameEquals(String productName) {
    return (root, query, cb) -> {
      if (productName == null)
        return null;

      Join<CartModel, CartItemModel> itemsJoin = root.join("cartItems", JoinType.INNER);
      Join<CartItemModel, ProductModel> productJoin = itemsJoin.join("product", JoinType.INNER);
      return cb.equal(productJoin.get("name"), productName);
    };
  }

  private Specification<CartModel> productNameContains(String productNameContains) {
    return (root, query, cb) -> {
      if (productNameContains == null)
        return null;

      Join<CartModel, CartItemModel> itemsJoin = root.join("cartItems", JoinType.INNER);
      Join<CartItemModel, ProductModel> productJoin = itemsJoin.join("product", JoinType.INNER);
      return cb.like(cb.lower(productJoin.get("name")), "%" + productNameContains.toLowerCase() + "%");
    };
  }

  private Specification<CartModel> itemQuantityRange(Integer minQuantity, Integer maxQuantity) {
    return (root, query, cb) -> {
      if (minQuantity == null && maxQuantity == null)
        return null;

      Join<CartModel, CartItemModel> itemsJoin = root.join("cartItems", JoinType.INNER);

      List<Predicate> predicates = new ArrayList<>();
      if (minQuantity != null) {
        predicates.add(cb.ge(itemsJoin.get("quantity"), minQuantity));
      }
      if (maxQuantity != null) {
        predicates.add(cb.le(itemsJoin.get("quantity"), maxQuantity));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private Specification<CartModel> itemPriceRange(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice) {
    return (root, query, cb) -> {
      if (minPrice == null && maxPrice == null)
        return null;

      Join<CartModel, CartItemModel> itemsJoin = root.join("cartItems", JoinType.INNER);
      Join<CartItemModel, ProductModel> productJoin = itemsJoin.join("product", JoinType.INNER);

      List<Predicate> predicates = new ArrayList<>();
      if (minPrice != null) {
        predicates.add(cb.ge(productJoin.get("unitPrice"), minPrice));
      }
      if (maxPrice != null) {
        predicates.add(cb.le(productJoin.get("unitPrice"), maxPrice));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private Specification<CartModel> discountRange(java.math.BigDecimal minDiscount, java.math.BigDecimal maxDiscount) {
    return (root, query, cb) -> {
      if (minDiscount == null && maxDiscount == null)
        return null;

      Join<CartModel, CartItemModel> itemsJoin = root.join("cartItems", JoinType.INNER);

      List<Predicate> predicates = new ArrayList<>();
      if (minDiscount != null) {
        predicates.add(cb.ge(itemsJoin.get("discountPerUnit"), minDiscount));
      }
      if (maxDiscount != null) {
        predicates.add(cb.le(itemsJoin.get("discountPerUnit"), maxDiscount));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  // Afterward item specifications
  private Specification<CartModel> afterwardProductIdsIn(List<String> productIds) {
    return (root, query, cb) -> {
      if (productIds == null || productIds.isEmpty())
        return null;

      Join<CartModel, AfterwardModel> afterwardJoin = root.join("afterwardItems", JoinType.INNER);
      return afterwardJoin.get("productId").in(productIds);
    };
  }

  private Specification<CartModel> afterwardQuantityRange(Integer minQuantity, Integer maxQuantity) {
    return (root, query, cb) -> {
      if (minQuantity == null && maxQuantity == null)
        return null;

      Join<CartModel, AfterwardModel> afterwardJoin = root.join("afterwardItems", JoinType.INNER);

      List<Predicate> predicates = new ArrayList<>();
      if (minQuantity != null) {
        predicates.add(cb.ge(afterwardJoin.get("quantity"), minQuantity));
      }
      if (maxQuantity != null) {
        predicates.add(cb.le(afterwardJoin.get("quantity"), maxQuantity));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private Specification<CartModel> afterwardAddedAfter(java.time.LocalDateTime addedAfter) {
    return (root, query, cb) -> {
      if (addedAfter == null)
        return null;

      Join<CartModel, AfterwardModel> afterwardJoin = root.join("afterwardItems", JoinType.INNER);
      return cb.greaterThanOrEqualTo(afterwardJoin.get("addedAt"), addedAfter);
    };
  }

  private Specification<CartModel> afterwardAddedBefore(java.time.LocalDateTime addedBefore) {
    return (root, query, cb) -> {
      if (addedBefore == null)
        return null;

      Join<CartModel, AfterwardModel> afterwardJoin = root.join("afterwardItems", JoinType.INNER);
      return cb.lessThanOrEqualTo(afterwardJoin.get("addedAt"), addedBefore);
    };
  }

  // Product specifications
  private Specification<CartModel> productAvailable(Boolean available) {
    return (root, query, cb) -> {
      if (available == null)
        return null;

      Join<CartModel, CartItemModel> itemsJoin = root.join("cartItems", JoinType.INNER);
      Join<CartItemModel, ProductModel> productJoin = itemsJoin.join("product", JoinType.INNER);
      return cb.equal(productJoin.get("isAvailable"), available);
    };
  }

  private Specification<CartModel> productPriceRange(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice) {
    return (root, query, cb) -> {
      if (minPrice == null && maxPrice == null)
        return null;

      Join<CartModel, CartItemModel> itemsJoin = root.join("cartItems", JoinType.INNER);
      Join<CartItemModel, ProductModel> productJoin = itemsJoin.join("product", JoinType.INNER);

      List<Predicate> predicates = new ArrayList<>();
      if (minPrice != null) {
        predicates.add(cb.ge(productJoin.get("unitPrice"), minPrice));
      }
      if (maxPrice != null) {
        predicates.add(cb.le(productJoin.get("unitPrice"), maxPrice));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
