package microservice.cart_service.infrastructure.adapter.outbound.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import microservice.cart_service.app.cart.core.domain.specficication.CartSearchCriteria;

public class CartSearchCriteriaTest {

  @Test
  void createCartSearchCriteriaWithBuilder() {
    // Given
    String cartId = "cart-123";
    String customerId = "customer-456";
    LocalDateTime createdAfter = LocalDateTime.now().minusDays(7);
    LocalDateTime createdBefore = LocalDateTime.now();
    List<String> productIds = Arrays.asList("product-1", "product-2");
    Boolean hasItems = true;
    Integer minItemQuantity = 1;
    Integer maxItemQuantity = 10;
    BigDecimal minItemPrice = new BigDecimal("10.00");
    BigDecimal maxItemPrice = new BigDecimal("100.00");

    // When
    CartSearchCriteria criteria = CartSearchCriteria.builder()
        .cartId(cartId)
        .customerId(customerId)
        .createdAfter(createdAfter)
        .createdBefore(createdBefore)
        .productIds(productIds)
        .hasItems(hasItems)
        .minItemQuantity(minItemQuantity)
        .maxItemQuantity(maxItemQuantity)
        .minItemPrice(minItemPrice)
        .maxItemPrice(maxItemPrice)
        .build();

    // Then
    assertThat(criteria).isNotNull();
    assertThat(criteria.cartId()).isEqualTo(cartId);
    assertThat(criteria.customerId()).isEqualTo(customerId);
    assertThat(criteria.createdAfter()).isEqualTo(createdAfter);
    assertThat(criteria.createdBefore()).isEqualTo(createdBefore);
    assertThat(criteria.productIds()).isEqualTo(productIds);
    assertThat(criteria.hasItems()).isEqualTo(hasItems);
    assertThat(criteria.minItemQuantity()).isEqualTo(minItemQuantity);
    assertThat(criteria.maxItemQuantity()).isEqualTo(maxItemQuantity);
    assertThat(criteria.minItemPrice()).isEqualTo(minItemPrice);
    assertThat(criteria.maxItemPrice()).isEqualTo(maxItemPrice);
  }

  @Test
  void createEmptyCartSearchCriteria() {
    // When
    CartSearchCriteria criteria = CartSearchCriteria.builder().build();

    // Then
    assertThat(criteria).isNotNull();
    assertThat(criteria.cartId()).isNull();
    assertThat(criteria.customerId()).isNull();
    assertThat(criteria.createdAfter()).isNull();
    assertThat(criteria.createdBefore()).isNull();
    assertThat(criteria.productIds()).isNull();
    assertThat(criteria.hasItems()).isNull();
    assertThat(criteria.minItemQuantity()).isNull();
    assertThat(criteria.maxItemQuantity()).isNull();
    assertThat(criteria.minItemPrice()).isNull();
    assertThat(criteria.maxItemPrice()).isNull();
  }

  @Test
  void createCartSearchCriteriaWithAfterwardsFilters() {
    // Given
    Boolean hasAfterwardItems = true;
    List<String> afterwardProductIds = Arrays.asList("afterward-1", "afterward-2");
    Integer minAfterwardQuantity = 1;
    Integer maxAfterwardQuantity = 5;
    LocalDateTime afterwardAddedAfter = LocalDateTime.now().minusDays(3);
    LocalDateTime afterwardAddedBefore = LocalDateTime.now();

    // When
    CartSearchCriteria criteria = CartSearchCriteria.builder()
        .hasAfterwardItems(hasAfterwardItems)
        .afterwardProductIds(afterwardProductIds)
        .minAfterwardQuantity(minAfterwardQuantity)
        .maxAfterwardQuantity(maxAfterwardQuantity)
        .afterwardAddedAfter(afterwardAddedAfter)
        .afterwardAddedBefore(afterwardAddedBefore)
        .build();

    // Then
    assertThat(criteria.hasAfterwardItems()).isEqualTo(hasAfterwardItems);
    assertThat(criteria.afterwardProductIds()).isEqualTo(afterwardProductIds);
    assertThat(criteria.minAfterwardQuantity()).isEqualTo(minAfterwardQuantity);
    assertThat(criteria.maxAfterwardQuantity()).isEqualTo(maxAfterwardQuantity);
    assertThat(criteria.afterwardAddedAfter()).isEqualTo(afterwardAddedAfter);
    assertThat(criteria.afterwardAddedBefore()).isEqualTo(afterwardAddedBefore);
  }

  @Test
  void createCartSearchCriteriaWithProductFilters() {
    // Given
    String productName = "Test Product";
    String productNameContains = "Test";
    BigDecimal minDiscountPerUnit = new BigDecimal("0.50");
    BigDecimal maxDiscountPerUnit = new BigDecimal("5.00");
    Integer minTotalItems = 1;
    Integer maxTotalItems = 50;

    // When
    CartSearchCriteria criteria = CartSearchCriteria.builder()
        .productName(productName)
        .productNameContains(productNameContains)
        .minDiscountPerUnit(minDiscountPerUnit)
        .maxDiscountPerUnit(maxDiscountPerUnit)
        .minTotalItems(minTotalItems)
        .maxTotalItems(maxTotalItems)
        .build();

    // Then
    assertThat(criteria.productName()).isEqualTo(productName);
    assertThat(criteria.productNameContains()).isEqualTo(productNameContains);
    assertThat(criteria.minDiscountPerUnit()).isEqualTo(minDiscountPerUnit);
    assertThat(criteria.maxDiscountPerUnit()).isEqualTo(maxDiscountPerUnit);
    assertThat(criteria.minTotalItems()).isEqualTo(minTotalItems);
    assertThat(criteria.maxTotalItems()).isEqualTo(maxTotalItems);
  }

  @Test
  void createCartSearchCriteriaWithTimeRangeFilters() {
    // Given
    LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
    LocalDateTime createdAfter = baseTime.minusDays(7);
    LocalDateTime createdBefore = baseTime;
    LocalDateTime updatedAfter = baseTime.minusDays(1);
    LocalDateTime updatedBefore = baseTime.plusDays(1);

    // When
    CartSearchCriteria criteria = CartSearchCriteria.builder()
        .createdAfter(createdAfter)
        .createdBefore(createdBefore)
        .updatedAfter(updatedAfter)
        .updatedBefore(updatedBefore)
        .build();

    // Then
    assertThat(criteria.createdAfter()).isEqualTo(createdAfter);
    assertThat(criteria.createdBefore()).isEqualTo(createdBefore);
    assertThat(criteria.updatedAfter()).isEqualTo(updatedAfter);
    assertThat(criteria.updatedBefore()).isEqualTo(updatedBefore);

    // Verify logical time relationships
    assertThat(criteria.createdAfter()).isBefore(criteria.createdBefore());
    assertThat(criteria.updatedAfter()).isBefore(criteria.updatedBefore());
  }

  @Test
  void cartSearchCriteriaRecord() {
    // Given
    CartSearchCriteria criteria1 = CartSearchCriteria.builder()
        .cartId("cart-1")
        .customerId("customer-1")
        .build();

    CartSearchCriteria criteria2 = CartSearchCriteria.builder()
        .cartId("cart-1")
        .customerId("customer-1")
        .build();

    CartSearchCriteria criteria3 = CartSearchCriteria.builder()
        .cartId("cart-2")
        .customerId("customer-1")
        .build();

    // Then - Test record behavior
    assertThat(criteria1).isEqualTo(criteria2);
    assertThat(criteria1).isNotEqualTo(criteria3);
    assertThat(criteria1.hashCode()).isEqualTo(criteria2.hashCode());
    assertThat(criteria1.toString()).contains("CartSearchCriteria");
    assertThat(criteria1.toString()).contains("cart-1");
    assertThat(criteria1.toString()).contains("customer-1");
  }
}
