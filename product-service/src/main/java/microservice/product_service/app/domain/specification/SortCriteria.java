package microservice.product_service.app.domain.specification;

public record SortCriteria(String field, String direction) {
  public static SortCriteria of(String field, String direction) {
    return new SortCriteria(field, direction);
  }

  public static SortCriteria ascending(String field) {
    return new SortCriteria(field, "ASC");
  }

  public static SortCriteria descending(String field) {
    return new SortCriteria(field, "DESC");
  }
}
