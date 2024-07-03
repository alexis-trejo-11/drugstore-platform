package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.Category.CategoryDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.Category.MainCategoryDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.Category.SubcategoryReturnDTO;
import microservice.product_service.Model.MainCategory;
import microservice.product_service.Repository.MainCategoryRepository;
import microservice.product_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MainCategoryService {

    private final MainCategoryRepository mainCategoryRepository;

    @Autowired
    public MainCategoryService(MainCategoryRepository mainCategoryRepository) {
        this.mainCategoryRepository = mainCategoryRepository;
    }

    @Async
    @Transactional
    public void insertCategory(MainCategoryDTO mainCategoryDTO) {
        try {
            MainCategory mainCategory = new MainCategory();
            mainCategory.setName(mainCategoryDTO.getName());

            LocalDateTime now = LocalDateTime.now();
            mainCategory.setCreatedAt(now);
            mainCategory.setUpdatedAt(now);

            mainCategoryRepository.saveAndFlush(mainCategory);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while inserting category", e);
        }
    }


    @Transactional
    @Async
    public MainCategoryDTO findMainCategoryByIdWithCategoryAndSubCategory(Long id) {
        try {
            Optional<MainCategory> mainCategory = mainCategoryRepository.findById(id);
            if (mainCategory.isEmpty()) {
                return null;

            }

            MainCategoryDTO mainCategoryDTO = ModelTransformer.mainCategorytoDTO(mainCategory.get());

            List<CategoryDTO> categoryDTOS = mainCategory.get().getCategories().stream()
                    .map(ModelTransformer::categoryToReturnDTO)
                    .collect(Collectors.toList());
            mainCategoryDTO.setCategoryDTOS(categoryDTOS);

            List<SubcategoryReturnDTO> subcategoryDTOS = mainCategory.get().getSubcategories().stream()
                    .map(ModelTransformer::subcategoryToReturnDTO)
                    .collect(Collectors.toList());
            mainCategoryDTO.setSubcategoriesDTOS(subcategoryDTOS);

            return mainCategoryDTO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Async
    public MainCategoryDTO findMainCategoryByIdWithProducts(Long mainCategoryId) {
        try {
            Optional<MainCategory> mainCategory =  mainCategoryRepository.findById(mainCategoryId);
            if (mainCategory.isEmpty()) {
                return null;
            }

            MainCategoryDTO mainCategoryDTO = ModelTransformer.mainCategorytoDTO(mainCategory.get());

            List<ProductDTO> productDTOS = mainCategory.get().getProducts().stream()
                    .map(ModelTransformer::productToDTO)
                    .collect(Collectors.toList());
            mainCategoryDTO.setProductsDTO(productDTOS);

            // Return DTO
            return mainCategoryDTO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public boolean updateMainCategory(MainCategoryDTO mainCategoryDTO) {
        try {
            Optional<MainCategory> mainCategoryOptional = mainCategoryRepository.findById(mainCategoryDTO.getId());
            if (mainCategoryOptional.isEmpty()) {
                return false;
            }

            MainCategory mainCategory = new MainCategory();
            mainCategory.setName(mainCategoryDTO.getName());
            mainCategory.setUpdatedAt(LocalDateTime.now());

            mainCategoryRepository.saveAndFlush(mainCategory);

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public boolean deleteCategoryById(Long categoryId) {
        try {
            Optional<MainCategory> mainCategory = mainCategoryRepository.findById(categoryId);
            if (mainCategory.isEmpty()) {
                return false;
            }

            mainCategoryRepository.deleteById(categoryId);

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}