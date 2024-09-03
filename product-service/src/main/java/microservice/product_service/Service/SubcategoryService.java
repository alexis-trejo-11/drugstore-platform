package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubCategoryInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubCategoryUpdateDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubcategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubcategoryService {
    Page<SubcategoryDTO> getAllSubCategories(Pageable pageable);
    SubcategoryDTO getSubCategoryByIdWithProducts(Long subcategoryId, Pageable pageable);
    void insertCategory(SubCategoryInsertDTO subCategoryInsertDTO);
    void updateCategory(SubCategoryUpdateDTO subCategoryUpdateDTO);
    void deleteCategory(Long subcategoryId);
    boolean validateExistingSubCategory(Long subcategoryID);
}


