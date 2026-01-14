package microservice.product_service.app.domain.specification;

import microservice.product_service.app.domain.model.enums.ProductCategory;

public record ProductSearchCriteria(
    String name,
    ProductCategory category,
    String manufacturer,
    Boolean requiresPrescription,
    Boolean onlyActive,
    Boolean excludeDeleted,
    SortCriteria sortBy,
    int page,
    int size) {
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String name;
    private ProductCategory category;
    private String manufacturer;
    private Boolean requiresPrescription;
    private Boolean onlyActive = true;
    private Boolean excludeDeleted = true;
    private SortCriteria sortBy;
    private int page = 0;
    private int size = 10;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder category(ProductCategory category) {
      this.category = category;
      return this;
    }

    public Builder manufacturer(String manufacturer) {
      this.manufacturer = manufacturer;
      return this;
    }

    public Builder requiresPrescription(Boolean requiresPrescription) {
      this.requiresPrescription = requiresPrescription;
      return this;
    }

    public Builder onlyActive(Boolean onlyActive) {
      this.onlyActive = onlyActive;
      return this;
    }

    public Builder excludeDeleted(Boolean excludeDeleted) {
      this.excludeDeleted = excludeDeleted;
      return this;
    }

    public Builder sortBy(SortCriteria sortBy) {
      this.sortBy = sortBy;
      return this;
    }

    public Builder page(int page) {
      this.page = page;
      return this;
    }

    public Builder size(int size) {
      this.size = size;
      return this;
    }

    public ProductSearchCriteria build() {
      return new ProductSearchCriteria(
          name,
          category,
          manufacturer,
          requiresPrescription,
          onlyActive,
          excludeDeleted,
          sortBy,
          page,
          size);
    }
  }
}
