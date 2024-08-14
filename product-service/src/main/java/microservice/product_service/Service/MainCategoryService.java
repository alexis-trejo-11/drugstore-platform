package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.MainCategoryDTO;

public interface MainCategoryService {
    void insertCategory(MainCategoryDTO mainCategoryDTO);
    MainCategoryDTO findMainCategoryByIdWithCategoryAndSubCategory(Long id);
    MainCategoryDTO findMainCategoryByIdWithProducts(Long mainCategoryId);
    boolean updateMainCategory(MainCategoryDTO mainCategoryDTO);
    boolean deleteCategoryById(Long categoryId);
    }
