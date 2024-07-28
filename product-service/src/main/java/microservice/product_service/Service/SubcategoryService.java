package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Product.Category.SubcategoryDTO;

import java.util.List;

public interface SubcategoryService {
    List<SubcategoryDTO> findAllSubCategories();
    void insertCategory(SubcategoryDTO subcategoryDTO);
    SubcategoryDTO findSubCategoryByIdWithProducts(Long subcategoryId);
    SubcategoryDTO updateCategory(Long subcategoryId, SubcategoryDTO subcategoryDTO);
    SubcategoryDTO deleteCategory(Long subcategoryId);
}


