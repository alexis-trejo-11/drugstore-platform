package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.CategoryDTO;

public interface CategoryService {
    void insertCategory(CategoryDTO categoryDTO);
    CategoryDTO findCategoryByIdWithProducts(Long id);
    CategoryDTO findCategoryByIdWithSubcategory(Long id);
    CategoryDTO findCategoryByIdWithSubCategoriesAndProducts(Long id);
    boolean updateCategory(CategoryDTO categoryDTO);
    boolean deleteCategory(Long categoryId);
    }
