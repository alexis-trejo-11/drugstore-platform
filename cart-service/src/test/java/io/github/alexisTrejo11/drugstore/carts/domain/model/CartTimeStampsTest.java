package io.github.alexisTrejo11.drugstore.carts.domain.model;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CartTimeStamps;

public class CartTimeStampsTest {

  @Test
  void nowCreatesTimestampsWithCurrentTime() {
    // Given
    LocalDateTime before = LocalDateTime.now().minusSeconds(1);

    // When
    CartTimeStamps timeStamps = CartTimeStamps.now();

    // Then
    LocalDateTime after = LocalDateTime.now().plusSeconds(1);
    assertThat(timeStamps.getCreatedAt()).isAfter(before);
    assertThat(timeStamps.getCreatedAt()).isBefore(after);
    assertThat(timeStamps.getUpdatedAt()).isAfter(before);
    assertThat(timeStamps.getUpdatedAt()).isBefore(after);
    assertThat(timeStamps.getDeletedAt()).isNull();
    assertThat(timeStamps.isDeleted()).isFalse();
  }

  @Test
  void reconstructWithAllParametersSucceeds() {
    // Given
    LocalDateTime createdAt = LocalDateTime.now().minusHours(2);
    LocalDateTime updatedAt = LocalDateTime.now().minusHours(1);
    LocalDateTime deletedAt = LocalDateTime.now().minusMinutes(30);

    // When
    CartTimeStamps timeStamps = CartTimeStamps.reconstruct(createdAt, updatedAt, deletedAt);

    // Then
    assertThat(timeStamps.getCreatedAt()).isEqualTo(createdAt);
    assertThat(timeStamps.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(timeStamps.getDeletedAt()).isEqualTo(deletedAt);
    assertThat(timeStamps.isDeleted()).isTrue();
  }

  @Test
  void reconstructWithNullCreatedAtUsesCurrentTime() {
    // Given
    LocalDateTime before = LocalDateTime.now().minusSeconds(1);
    LocalDateTime updatedAt = LocalDateTime.now().minusHours(1);

    // When
    CartTimeStamps timeStamps = CartTimeStamps.reconstruct(null, updatedAt, null);

    // Then
    LocalDateTime after = LocalDateTime.now().plusSeconds(1);
    assertThat(timeStamps.getCreatedAt()).isAfter(before);
    assertThat(timeStamps.getCreatedAt()).isBefore(after);
    assertThat(timeStamps.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(timeStamps.getDeletedAt()).isNull();
  }

  @Test
  void reconstructWithNullUpdatedAtUsesCurrentTime() {
    // Given
    LocalDateTime before = LocalDateTime.now().minusSeconds(1);
    LocalDateTime createdAt = LocalDateTime.now().minusHours(2);

    // When
    CartTimeStamps timeStamps = CartTimeStamps.reconstruct(createdAt, null, null);

    // Then
    LocalDateTime after = LocalDateTime.now().plusSeconds(1);
    assertThat(timeStamps.getCreatedAt()).isEqualTo(createdAt);
    assertThat(timeStamps.getUpdatedAt()).isAfter(before);
    assertThat(timeStamps.getUpdatedAt()).isBefore(after);
    assertThat(timeStamps.getDeletedAt()).isNull();
  }

  @Test
  void markAsUpdatedSetsCurrentTime() {
    // Given
    CartTimeStamps timeStamps = CartTimeStamps.now();
    LocalDateTime originalUpdatedAt = timeStamps.getUpdatedAt();

    // Small delay to ensure different timestamp
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // When
    timeStamps.markAsUpdated();

    // Then
    assertThat(timeStamps.getUpdatedAt()).isAfter(originalUpdatedAt);
  }

  @Test
  void markAsDeletedSetsCurrentTimeAndIsDeleted() {
    // Given
    CartTimeStamps timeStamps = CartTimeStamps.now();
    assertThat(timeStamps.isDeleted()).isFalse();

    // When
    timeStamps.markAsDeleted();

    // Then
    assertThat(timeStamps.getDeletedAt()).isNotNull();
    assertThat(timeStamps.isDeleted()).isTrue();
  }

  @Test
  void restoreClearsDeletedAt() {
    // Given
    CartTimeStamps timeStamps = CartTimeStamps.now();
    timeStamps.markAsDeleted();
    assertThat(timeStamps.isDeleted()).isTrue();

    // When
    timeStamps.restore();

    // Then
    assertThat(timeStamps.getDeletedAt()).isNull();
    assertThat(timeStamps.isDeleted()).isFalse();
  }

  @Test
  void isDeletedReturnsFalseWhenNotDeleted() {
    // Given
    CartTimeStamps timeStamps = CartTimeStamps.now();

    // Then
    assertThat(timeStamps.isDeleted()).isFalse();
  }

  @Test
  void isDeletedReturnsTrueWhenDeleted() {
    // Given
    CartTimeStamps timeStamps = CartTimeStamps.now();
    timeStamps.markAsDeleted();

    // Then
    assertThat(timeStamps.isDeleted()).isTrue();
  }

  @Test
  void toStringContainsAllTimestamps() {
    // Given
    CartTimeStamps timeStamps = CartTimeStamps.now();
    timeStamps.markAsDeleted();

    // When
    String timestampsString = timeStamps.toString();

    // Then
    assertThat(timestampsString).contains("CartTimeStamps");
    assertThat(timestampsString).contains("createdAt");
    assertThat(timestampsString).contains("updatedAt");
    assertThat(timestampsString).contains("deletedAt");
  }

  @Test
  void multipleUpdatesChangeUpdatedAt() {
    // Given
    CartTimeStamps timeStamps = CartTimeStamps.now();
    LocalDateTime originalUpdatedAt = timeStamps.getUpdatedAt();

    // When - Multiple updates with delays
    try {
      Thread.sleep(10);
      timeStamps.markAsUpdated();
      LocalDateTime firstUpdate = timeStamps.getUpdatedAt();

      Thread.sleep(10);
      timeStamps.markAsUpdated();
      LocalDateTime secondUpdate = timeStamps.getUpdatedAt();

      // Then
      assertThat(firstUpdate).isAfter(originalUpdatedAt);
      assertThat(secondUpdate).isAfter(firstUpdate);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
