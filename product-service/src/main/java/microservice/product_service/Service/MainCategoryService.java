package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.MainCategoryDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.MainCategoryInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.MainCategoryUpdateDTO;
import org.springframework.data.domain.Pageable;

public interface MainCategoryService {


    MainCategoryDTO getMainCategoryByIdWithCategoryAndSubCategory(Long mainCategoryID, Pageable pageable);
    MainCategoryDTO getMainCategoryByIdWithProducts(Long mainCategoryId, Pageable pageable);
    boolean validateExistingMainCategory(Long mainCategoryID);
    void insertCategory(MainCategoryInsertDTO mainCategoryInsertDTO);
    void updateMainCategory(MainCategoryUpdateDTO mainCategoryUpdateDTO);
    void deleteMainCategoryById(Long categoryId);
    }
