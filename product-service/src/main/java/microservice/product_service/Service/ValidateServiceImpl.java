package microservice.product_service.Service;

import microservice.product_service.Repository.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ValidateServiceImpl implements ValidateService {

    private final MainCategoryRepository mainCategoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public ValidateServiceImpl(MainCategoryRepository mainCategoryRepository,
                               SubcategoryRepository subcategoryRepository,
                               ProductRepository productRepository,
                               CategoryRepository categoryRepository,
                               SupplierRepository supplierRepository) {
        this.mainCategoryRepository = mainCategoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    @Cacheable(value = "validateExisitingProduct", key = "#productId")
    public boolean validateExisitingProduct(Long productId) {
        return productRepository.findById(productId).isPresent();
    }

    @Override
    @Cacheable(value = "validateExistingSupplier", key = "#supplierId")
    public boolean validateExistingSupplier(Long supplierId) {
        return supplierRepository.findById(supplierId).isPresent();

    }

    @Override
    public boolean validateExistingCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).isPresent();
    }

    @Override
    public boolean validateExistingMainCategory(Long mainCategoryId) {
        return mainCategoryRepository.findById(mainCategoryId).isPresent();
    }

    @Override
    public boolean validateExistingSubCategory(Long subcategoryId) {
        return subcategoryRepository.findById(subcategoryId).isPresent();
    }

}
