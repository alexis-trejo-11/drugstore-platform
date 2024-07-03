package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.Category.CategoryDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.Category.SubcategoryReturnDTO;
import microservice.product_service.Model.Category;
import microservice.product_service.Repository.CategoryRepository;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Create
    @Async
    @Transactional
    public void insertCategory(CategoryDTO categoryDTO) {
        try {
            Category category = new Category(categoryDTO);

            LocalDateTime now = LocalDateTime.now();
            category.setCreatedAt(now);
            category.setUpdatedAt(now);

            categoryRepository.saveAndFlush(category);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while inserting category", e);
        }
    }

    @Async
    @Transactional
    public CategoryDTO findCategoryByIdWithProducts(Long id) {
        try {
            Optional<Category> category = categoryRepository.findById(id);
            if (category.isEmpty()) {
                return null;
            }

            CategoryDTO categoryDTO = ModelTransformer.categoryToReturnDTO(category.get());
            List<ProductDTO> productInsertDTOS = category.get().getProducts().stream()
                    .map(ModelTransformer::productToDTO)
                    .collect(Collectors.toList());
            categoryDTO.setProductsDTO(productInsertDTOS);

            return categoryDTO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Async
    public CategoryDTO findCategoryByIdWithSubcategory(Long id) {
        try {
            Optional<Category> category = categoryRepository.findById(id);
            if (category.isEmpty()) {
                return null;
            }

            CategoryDTO categoryDTO = ModelTransformer.categoryToReturnDTO(category.get());

            List<SubcategoryReturnDTO> subcategoryDTOS = category.get().getSubCategories().stream()
                    .map(ModelTransformer::subcategoryToReturnDTO)
                    .collect(Collectors.toList());
            categoryDTO.setSubcategoriesDTOS(subcategoryDTOS);

            return categoryDTO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Async
    public CategoryDTO findCategoryByIdWithSubCategoriesAndProducts(Long id) {
        try {
            Optional<Category> category = categoryRepository.findById(id);
            if (category.isEmpty()) {
               return null;
            }

            CategoryDTO categoryDTO = ModelTransformer.categoryToReturnDTO(category.get());

            List<ProductDTO> productDTOS = category.get().getProducts().stream()
                    .map(ModelTransformer::productToDTO)
                    .collect(Collectors.toList());
            categoryDTO.setProductsDTO(productDTOS);

            List<SubcategoryReturnDTO> subcategoryDTOS = category.get().getSubCategories().stream()
                    .map(ModelTransformer::subcategoryToReturnDTO)
                    .collect(Collectors.toList());
            categoryDTO.setSubcategoriesDTOS(subcategoryDTOS);

            return categoryDTO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @Async
    public boolean updateCategory(CategoryDTO categoryDTO) {
        try {
            Optional<Category> optionalCategory = categoryRepository.findById(categoryDTO.getId());
            if (optionalCategory.isEmpty()) {
                return false;
            }

            Category categoryUpdated = new Category(categoryDTO);

            categoryRepository.saveAndFlush(categoryUpdated);

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public boolean deleteCategory(Long categoryId) {
        try {
            Optional<Category> category = categoryRepository.findById(categoryId);
            if (category.isEmpty()) {
                return false;
            }

            categoryRepository.deleteById(categoryId);

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
