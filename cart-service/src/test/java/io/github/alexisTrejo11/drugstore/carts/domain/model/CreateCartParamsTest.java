package io.github.alexisTrejo11.drugstore.carts.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.CreateCartParams;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;

public class CreateCartParamsTest {

  @Test
  void createWithValidCustomerIdSucceeds() {
    // Given
    CustomerId customerId = CustomerId.from("customer-123");

    // When
    CreateCartParams params = new CreateCartParams(customerId);

    // Then
    assertThat(params).isNotNull();
    assertThat(params.customerId()).isEqualTo(customerId);
  }

  @Test
  void fromCustomerIdStringSucceeds() {
    // Given
    String customerIdValue = "customer-456";

    // When
    CreateCartParams params = CreateCartParams.fromCustomerIdString(customerIdValue);

    // Then
    assertThat(params).isNotNull();
    assertThat(params.customerId().value()).isEqualTo(customerIdValue);
  }

  @Test
  void recordEqualsAndHashCodeWork() {
    // Given
    CustomerId customerId = CustomerId.from("customer-789");
    CreateCartParams params1 = new CreateCartParams(customerId);
    CreateCartParams params2 = new CreateCartParams(customerId);
    CreateCartParams params3 = new CreateCartParams(CustomerId.from("different-customer"));

    // Then
    assertThat(params1).isEqualTo(params2);
    assertThat(params1.hashCode()).isEqualTo(params2.hashCode());
    assertThat(params1).isNotEqualTo(params3);
  }

  @Test
  void toStringContainsCustomerId() {
    // Given
    CustomerId customerId = CustomerId.from("customer-test");
    CreateCartParams params = new CreateCartParams(customerId);

    // When
    String paramsString = params.toString();

    // Then
    assertThat(paramsString).contains("customer-test");
    assertThat(paramsString).contains("CreateCartParams");
  }

  @Test
  void canCreateWithNullCustomerId() {
    // When
    CreateCartParams params = new CreateCartParams(null);

    // Then
    assertThat(params.customerId()).isNull();
  }
}
