package microservice.product_service.Service;


import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.DTO.Product.Category.CategoryReturnDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.Category.SubcategoryReturnDTO;
import microservice.product_service.Model.Category;
import microservice.product_service.Repository.CategoryRepository;
import microservice.product_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
    public CompletableFuture<Result<CategoryReturnDTO>> insertCategory(CategoryReturnDTO categoryDTO) {
        try {
            // Create a new Category object and transform DTO into Model using the constructor
                Category category = new Category(categoryDTO);

            // Set Model Dates
            LocalDateTime now = LocalDateTime.now();
            category.setCreatedAt(now);
            category.setUpdatedAt(now);

            // Save to the database
            categoryRepository.saveAndFlush(category);

            // Return DTO
            CategoryReturnDTO categoryReturnDTO = ModelTransformer.categoryToReturnDTO(category);
            return CompletableFuture.completedFuture(Result.success(categoryReturnDTO));
        } catch (DataIntegrityViolationException e) {
            // Handle the data integrity violation exception
            throw new DataIntegrityViolationException("Error occurred while inserting category: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected exception
            throw new RuntimeException("An unexpected error occurred while inserting category", e);
        }
    }

    // Read
    @Async
    @Transactional
    public CompletableFuture<Result<CategoryReturnDTO>> findCategoryByIdWithProducts(Long id) {
        try {
            // Attempt to find the category by its ID
            Optional<Category> category = categoryRepository.findById(id);

            // If the category is present, transform it into a DTO and return it
            if (category.isPresent()) {
                CategoryReturnDTO categoryReturnDTO = ModelTransformer.categoryToReturnDTO(category.get());

                // Set products for the CategoryDTO
                List<ProductDTO> productInsertDTOS = category.get().getProducts().stream()
                        .map(ModelTransformer::productToDTO)
                        .collect(Collectors.toList());
                categoryReturnDTO.setProductsDTO(productInsertDTOS);

                // Return DTO
                return CompletableFuture.completedFuture(Result.success(categoryReturnDTO));
            } else {
                // If the category is not found, return Bad Result
                return CompletableFuture.completedFuture(Result.error("Category with ID: " + id.toString() + " not found."));
            }
        } catch (Exception e) {
            // Handle other exceptions and return a failed CompletableFuture with an error message
            return CompletableFuture.failedFuture(e);
        }
    }

    @Transactional
    @Async
    public CompletableFuture<Result<CategoryReturnDTO>> findCategoryByIdWithSubcategory(Long id) {
        try {
            // Attempt to find the category by its ID
            Optional<Category> category = categoryRepository.findById(id);

            // If the category is present, transform it into a DTO and return it
            if (category.isPresent()) {
                    CategoryReturnDTO categoryReturnDTO = ModelTransformer.categoryToReturnDTO(category.get());

                // Set products for the CategoryDTO
                List<SubcategoryReturnDTO> subcategoryDTOS = category.get().getSubCategories().stream()
                        .map(ModelTransformer::subcategoryToReturnDTO)
                        .collect(Collectors.toList());
                categoryReturnDTO.setSubcategoriesDTOS(subcategoryDTOS);

                // Return DTO
                return CompletableFuture.completedFuture(Result.success(categoryReturnDTO));
            } else {
                // If the category is not found, return an error result
                return CompletableFuture.completedFuture(Result.error("Category with ID: " + id + " not found."));
            }
        } catch (Exception e) {
            // Handle other exceptions and return a failed CompletableFuture with an error message
            return CompletableFuture.completedFuture(Result.error(e.getMessage()));
        }
    }

    @Transactional
    @Async
    public CompletableFuture<Result<CategoryReturnDTO>> findCategoryByIdWithSubCategoriesAndProducts(Long id) {
        try {
            // Attempt to find the category by its ID
            Optional<Category> category = categoryRepository.findById(id);

            // If the category is present, transform it into a DTO and return it
            if (category.isPresent()) {
                CategoryReturnDTO categoryReturnDTO = ModelTransformer.categoryToReturnDTO(category.get());

                // Set products for the CategoryDTO
                List<ProductDTO> productDTOS = category.get().getProducts().stream()
                        .map(ModelTransformer::productToDTO)
                        .collect(Collectors.toList());
                categoryReturnDTO.setProductsDTO(productDTOS);

                // Set subcategories for the CategoryDTO
                List<SubcategoryReturnDTO> subcategoryDTOS = category.get().getSubCategories().stream()
                        .map(ModelTransformer::subcategoryToReturnDTO)
                        .collect(Collectors.toList());
                categoryReturnDTO.setSubcategoriesDTOS(subcategoryDTOS);

                // Return DTO
                return CompletableFuture.completedFuture(Result.success(categoryReturnDTO));
            } else {
                // If the category is not found, return an error result
                return CompletableFuture.completedFuture(Result.error("Category with ID: " + id + " not found."));
            }
        } catch (Exception e) {
            // Handle other exceptions and return a failed CompletableFuture with an error message
            return CompletableFuture.completedFuture(Result.error(e.getMessage()));
        }
    }

    // Update
    @Transactional
    @Async
    public CompletableFuture<Result<CategoryReturnDTO>> updateCategory(Long id, CategoryReturnDTO categoryDTO) {
        try {
            Optional<Category> optionalCategory = categoryRepository.findById(id);
            if (optionalCategory.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Category with ID: " + id.toString() + " not found."));
            }
            // Update category properties using constructor
            Category categoryUpdated = new Category(categoryDTO);

            // Save the updated category
            Category updatedCategory = categoryRepository.save(categoryUpdated);

            return CompletableFuture.completedFuture(Result.success(ModelTransformer.categoryToReturnDTO(updatedCategory)));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(Result.error(e.getMessage()));
        }
    }

    // Delete
    @Async
    @Transactional
    public CompletableFuture<Result<CategoryReturnDTO>> deleteCategory(Long id) {
        try {
            Optional<Category> category = categoryRepository.findById(id);
            if (category.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Category with ID: " + id.toString() + " not found."));
            }

            // Make DTO before removing the entity
            CategoryReturnDTO categoryReturnDTO = ModelTransformer.categoryToReturnDTO(category.get());

            // Delete the category
            categoryRepository.delete(category.get());

            return CompletableFuture.completedFuture(Result.success(categoryReturnDTO));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(Result.error(e.getMessage()));
        }
    }


}
