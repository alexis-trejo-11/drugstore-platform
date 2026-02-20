package io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductValueObjectException;

/**
 * Represents a collection of product images.
 * The first image in the list is considered the primary/main image.
 */
public record ProductImages(List<String> urls) {

  public static final ProductImages EMPTY = new ProductImages(Collections.emptyList());
  private static final int MAX_IMAGES = 10;
  private static final int MAX_URL_LENGTH = 500;

  public ProductImages {
    Objects.requireNonNull(urls, "Image URLs list cannot be null");
    urls = List.copyOf(urls); // Defensive immutable copy
  }

  public static ProductImages create(List<String> urls) {
    if (urls == null || urls.isEmpty()) {
      return EMPTY;
    }

    if (urls.size() > MAX_IMAGES) {
      throw new ProductValueObjectException("ProductImages",
          "Cannot have more than " + MAX_IMAGES + " images");
    }

    List<String> validatedUrls = new ArrayList<>();
    for (String url : urls) {
      String validated = validateAndNormalizeUrl(url);
      if (validated != null) {
        validatedUrls.add(validated);
      }
    }

    return new ProductImages(validatedUrls);
  }

  public static ProductImages of(String... urls) {
    return create(List.of(urls));
  }

  public static ProductImages single(String url) {
    return create(List.of(url));
  }

  private static String validateAndNormalizeUrl(String url) {
    if (url == null || url.trim().isEmpty()) {
      return null;
    }

    String trimmed = url.trim();

    if (trimmed.length() > MAX_URL_LENGTH) {
      throw new ProductValueObjectException("ProductImages",
          "Image URL cannot exceed " + MAX_URL_LENGTH + " characters");
    }

    // Basic URL format validation
    if (!trimmed.matches("^(https?://|/).+")) {
      throw new ProductValueObjectException("ProductImages",
          "Invalid image URL format: " + trimmed);
    }

    return trimmed;
  }

  public boolean isEmpty() {
    return urls.isEmpty();
  }

  public int count() {
    return urls.size();
  }

  public String getPrimaryImage() {
    return urls.isEmpty() ? null : urls.get(0);
  }

  public List<String> getSecondaryImages() {
    if (urls.size() <= 1) {
      return Collections.emptyList();
    }
    return urls.subList(1, urls.size());
  }

  public ProductImages addImage(String url) {
    if (urls.size() >= MAX_IMAGES) {
      throw new ProductValueObjectException("ProductImages",
          "Cannot add more images. Maximum is " + MAX_IMAGES);
    }
    String validated = validateAndNormalizeUrl(url);
    if (validated == null) {
      return this;
    }
    List<String> newUrls = new ArrayList<>(urls);
    newUrls.add(validated);
    return new ProductImages(newUrls);
  }

  public ProductImages removeImage(String url) {
    if (!urls.contains(url)) {
      return this;
    }
    List<String> newUrls = new ArrayList<>(urls);
    newUrls.remove(url);
    return new ProductImages(newUrls);
  }

  public ProductImages setPrimaryImage(String url) {
    String validated = validateAndNormalizeUrl(url);
    if (validated == null) {
      throw new ProductValueObjectException("ProductImages", "Primary image URL cannot be empty");
    }
    List<String> newUrls = new ArrayList<>();
    newUrls.add(validated);
    for (String existing : urls) {
      if (!existing.equals(validated)) {
        newUrls.add(existing);
      }
    }
    return new ProductImages(newUrls);
  }

  public List<String> getUrls() {
    return urls;
  }
}
