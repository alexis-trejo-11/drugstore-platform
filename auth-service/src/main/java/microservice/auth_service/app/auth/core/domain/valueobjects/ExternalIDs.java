package microservice.auth_service.app.auth.core.domain.valueobjects;

public record ExternalIDs(String employeeId, String customerId, String supplierId) {
  public boolean hasAnyExternalId() {
    return employeeId != null || customerId != null || supplierId != null;
  }

  public boolean hasEmployeeId() {
    return employeeId != null;
  }

  public boolean hasCustomerId() {
    return customerId != null;
  }

  public boolean hasSupplierId() {
    return supplierId != null;
  }
}
