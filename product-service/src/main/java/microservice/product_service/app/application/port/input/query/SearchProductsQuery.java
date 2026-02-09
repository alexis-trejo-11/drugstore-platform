package microservice.product_service.app.application.port.input.query;

import microservice.product_service.app.domain.model.enums.ProductCategory;

public class SearchProductsQuery {
  private String name;
  private ProductCategory category;
  private String manufacturer;
  private Boolean requiresPrescription;
  private Boolean onlyAvailable;
  private int page = 0;
  private int size = 10;

  public SearchProductsQuery() {
  }

  public SearchProductsQuery(String name,
      ProductCategory category,
      String manufacturer,
      Boolean requiresPrescription,
      Boolean onlyAvailable,
      int page,
      int size) {
    this.name = name;
    this.category = category;
    this.manufacturer = manufacturer;
    this.requiresPrescription = requiresPrescription;
    this.onlyAvailable = onlyAvailable;
    this.page = page;
    this.size = size;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ProductCategory getCategory() {
    return category;
  }

  public void setCategory(ProductCategory category) {
    this.category = category;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public Boolean getRequiresPrescription() {
    return requiresPrescription;
  }

  public void setRequiresPrescription(Boolean requiresPrescription) {
    this.requiresPrescription = requiresPrescription;
  }

  public Boolean getOnlyAvailable() {
    return onlyAvailable;
  }

  public void setOnlyAvailable(Boolean onlyAvailable) {
    this.onlyAvailable = onlyAvailable;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }
}
