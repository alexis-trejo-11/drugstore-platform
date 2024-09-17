package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.CategoryDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.CategoryInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.CategoryUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryService {
    Page<CategoryDTO> findCategoryByIdWithProducts(Long categoryID, Pageable pageable);
    Page<CategoryDTO> findCategoryByIdWithSubcategory(Long categoryID, Pageable pageable);
    Page<CategoryDTO> findCategoryByIdWithSubCategoriesAndProducts(Long categoryID, Pageable pageable);

    Result<Void> insertCategory(CategoryInsertDTO categoryInsertDTO);
    Result<Void> updateCategory(CategoryUpdateDTO categoryUpdateDTO);
    void deleteCategory(Long categoryId);
    }
