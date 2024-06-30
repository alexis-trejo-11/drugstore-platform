package microservice.product_service.Service;


import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.DTO.Product.Category.CategoryReturnDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.Category.MainCategoryDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.Category.SubcategoryReturnDTO;
import microservice.product_service.Model.MainCategory;
import microservice.product_service.Repository.MainCategoryRepository;
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
public class MainCategoryService {

    private final MainCategoryRepository mainCategoryRepository;

    @Autowired
    public MainCategoryService(MainCategoryRepository mainCategoryRepository) {
        this.mainCategoryRepository = mainCategoryRepository;
    }

    @Async
    @Transactional
    public CompletableFuture<Result<MainCategoryDTO>> insertCategory(MainCategoryDTO mainCategoryDTO) {
        try {
            // Create a new Category object and transform DTO into Model using the constructor
            MainCategory mainCategory = new MainCategory();
            mainCategory.setName(mainCategory.getName());

            // Set Model Dates
            LocalDateTime now = LocalDateTime.now();
            mainCategory.setCreatedAt(now);
            mainCategory.setUpdatedAt(now);

            // Save to the database
            mainCategoryRepository.saveAndFlush(mainCategory);

            // Return DTO
            MainCategoryDTO mainCategoryReturnDTO = new MainCategoryDTO();
            return CompletableFuture.completedFuture(Result.success(mainCategoryReturnDTO));
        } catch (DataIntegrityViolationException e) {
            // Handle the data integrity violation exception
            throw new DataIntegrityViolationException("Error occurred while inserting category: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other unexpected exception
            throw new RuntimeException("An unexpected error occurred while inserting category", e);
        }
    }


    @Transactional
    @Async
    public CompletableFuture<Result<MainCategoryDTO>> findMainCategoryByIdWithCategoryAndSubCategory(Long id) {
        try {
            // Attempt to find the category by its ID
            Optional<MainCategory> mainCategory = mainCategoryRepository.findById(id);

            // If the category is present, transform it into a DTO and return it
            if (mainCategory.isPresent()) {
                MainCategoryDTO mainCategoryDTO = ModelTransformer.mainCategorytoDTO(mainCategory.get());

                // Set Categories and SubCategories for the CategoryDTO
                List<CategoryReturnDTO> categoryDTOS = mainCategory.get().getCategories().stream()
                        .map(ModelTransformer::categoryToReturnDTO)
                        .collect(Collectors.toList());
                mainCategoryDTO.setCategoryDTOS(categoryDTOS);

                List<SubcategoryReturnDTO> subcategoryDTOS = mainCategory.get().getSubcategories().stream()
                        .map(ModelTransformer::subcategoryToReturnDTO)
                        .collect(Collectors.toList());
                mainCategoryDTO.setSubcategoriesDTOS(subcategoryDTOS);

                // Return DTO
                return CompletableFuture.completedFuture(Result.success(mainCategoryDTO));
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
    public CompletableFuture<Result<MainCategoryDTO>> findMainCategoryByIdWithProducts(Long id) {
        try {
            // Attempt to find the category by its ID
            Optional<MainCategory> mainCategory =  mainCategoryRepository.findById(id);

            // If the category is present, transform it into a DTO and return it
            if (mainCategory.isPresent()) {
                MainCategoryDTO mainCategoryDTO = ModelTransformer.mainCategorytoDTO(mainCategory.get());

                // Set products for the CategoryDTO
                List<ProductDTO> productDTOS = mainCategory.get().getProducts().stream()
                        .map(ModelTransformer::productToDTO)
                        .collect(Collectors.toList());
                mainCategoryDTO.setProductsDTO(productDTOS);

                // Return DTO
                return CompletableFuture.completedFuture(Result.success(mainCategoryDTO));
            } else {
                // If the category is not found, return Bad Result
                return CompletableFuture.completedFuture(Result.error("Category with ID: " + id.toString() + " not found."));
            }
        } catch (Exception e) {
            // Handle other exceptions and return a failed CompletableFuture with an error message
            return CompletableFuture.failedFuture(e);
        }
    }


    // Update
    @Async
    @Transactional
    public CompletableFuture<Result<MainCategoryDTO>> updateMainCategory(Long id, MainCategoryDTO mainCategoryDTO) {
        try {
            Optional<MainCategory> mainCategoryOptional = mainCategoryRepository.findById(id);
            if (mainCategoryOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Category with ID: " + id.toString() + " not found."));
            }
            // Update category properties using constructor
            MainCategory mainCategory = new MainCategory();
            mainCategory.setName(mainCategoryDTO.getName());
            mainCategory.setUpdatedAt(LocalDateTime.now());

            // Save the updated category
            MainCategory  mainCategoryUpdated = mainCategoryRepository.save(mainCategory);

            return CompletableFuture.completedFuture(Result.success(ModelTransformer.mainCategorytoDTO(mainCategoryUpdated)));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(Result.error(e.getMessage()));
        }
    }

    // Delete
    @Async
    @Transactional
    public CompletableFuture<Result<MainCategoryDTO>> deleteCategory(Long id) {
        try {
            Optional<MainCategory> mainCategory = mainCategoryRepository.findById(id);
            if (mainCategory.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Category with ID: " + id.toString() + " not found."));
            }

            // Make DTO before removing the entity
            MainCategoryDTO mainCategoryDTO = ModelTransformer.mainCategorytoDTO(mainCategory.get());


            // Delete the category
            mainCategoryRepository.delete(mainCategory.get());

            return CompletableFuture.completedFuture(Result.success(mainCategoryDTO));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(Result.error(e.getMessage()));
        }
    }
}
