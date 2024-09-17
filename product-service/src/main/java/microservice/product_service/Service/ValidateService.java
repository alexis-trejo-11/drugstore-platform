package microservice.product_service.Service;

public interface ValidateService {
    boolean validateExisitingProduct(Long productId);
    boolean validateExistingSupplier(Long supplierId);
    boolean validateExistingCategory(Long categoryId);
    boolean validateExistingMainCategory(Long mainCategoryId);
    boolean validateExistingSubCategory(Long subcategoryId);


}
