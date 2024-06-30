package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.DTO.Product.Category.SubcategoryReturnDTO;
import microservice.product_service.Model.Subcategory;
import microservice.product_service.Repository.SubcategoryRepository;
import microservice.product_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class SubcategoryService {

    private final SubcategoryRepository subcategoryRepository;

    @Autowired
    public SubcategoryService(SubcategoryRepository subcategoryRepository) {
        this.subcategoryRepository = subcategoryRepository;
    }

    // Create
    @Async
    @Transactional
    public CompletableFuture<Result<SubcategoryReturnDTO>> insertCategory(SubcategoryReturnDTO subcategoryDTO) {
        try {
            Subcategory subcategory = new Subcategory(subcategoryDTO);
            subcategoryRepository.saveAndFlush(subcategory);
            SubcategoryReturnDTO subcategoryReturnDTO = ModelTransformer.subcategoryToReturnDTO(subcategory);
            return CompletableFuture.completedFuture(Result.success(subcategoryReturnDTO));
        } catch (DataIntegrityViolationException e) {
            return CompletableFuture.completedFuture(Result.error("Error occurred while inserting category: " + e.getMessage()));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(Result.error("An unexpected error occurred while inserting category: " + e.getMessage()));
        }
    }

    // Read
    @Async
    @Transactional
    public CompletableFuture<Result<List<SubcategoryReturnDTO>>> findAllSubCategories() {
        try {
            List<Subcategory> subcategories = subcategoryRepository.findAll();
            List<SubcategoryReturnDTO> subcategoryDTOs = subcategories.stream()
                    .map(ModelTransformer::subcategoryToReturnDTO)
                    .collect(Collectors.toList());
            return CompletableFuture.completedFuture(Result.success(subcategoryDTOs));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(Result.error("Failed to fetch subcategories: " + e.getMessage()));
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Result<SubcategoryReturnDTO>> findSubCategoryByIdWithProducts(Long id) {
        try {
            Optional<Subcategory> subcategoryOpt = subcategoryRepository.findById(id);
            if (subcategoryOpt.isPresent()) {
                Subcategory subcategory = subcategoryOpt.get();
                SubcategoryReturnDTO subcategoryReturnDTO = ModelTransformer.subcategoryToReturnDTO(subcategory);

                List<ProductDTO> productDTOs = subcategory.getProducts().stream()
                        .map(ModelTransformer::productToDTO)
                        .collect(Collectors.toList());
                subcategoryReturnDTO.setProductInsertDTOS(productDTOs);

                return CompletableFuture.completedFuture(Result.success(subcategoryReturnDTO));
            } else {
                return CompletableFuture.completedFuture(Result.error("Category with ID: " + id + " not found."));
            }
        } catch (Exception e) {
            return CompletableFuture.completedFuture(Result.error("Failed to fetch subcategory: " + e.getMessage()));
        }
    }

    // Update
    @Async
    @Transactional
    public CompletableFuture<Result<SubcategoryReturnDTO>> updateCategory(Long id, SubcategoryReturnDTO subcategoryDTO) {
        try {
            Optional<Subcategory> optionalSubcategory = subcategoryRepository.findById(id);
            if (optionalSubcategory.isPresent()) {
                Subcategory subcategory = optionalSubcategory.get();
                subcategory.updateFromDTO(subcategoryDTO);
                subcategoryRepository.save(subcategory);
                SubcategoryReturnDTO subcategoryReturnDTO = ModelTransformer.subcategoryToReturnDTO(subcategory);
                return CompletableFuture.completedFuture(Result.success(subcategoryReturnDTO));
            } else {
                return CompletableFuture.completedFuture(Result.error("Category with ID: " + id + " not found."));
            }
        } catch (Exception e) {
            return CompletableFuture.completedFuture(Result.error("Failed to update subcategory: " + e.getMessage()));
        }
    }

    // Delete
    @Async
    @Transactional
    public CompletableFuture<Result<SubcategoryReturnDTO>> deleteCategory(Long id) {
        try {
            Optional<Subcategory> optionalSubcategory = subcategoryRepository.findById(id);
            if (optionalSubcategory.isPresent()) {
                Subcategory subcategory = optionalSubcategory.get();
                SubcategoryReturnDTO subcategoryReturnDTO = ModelTransformer.subcategoryToReturnDTO(subcategory);
                subcategoryRepository.delete(subcategory);
                return CompletableFuture.completedFuture(Result.success(subcategoryReturnDTO));
            } else {
                return CompletableFuture.completedFuture(Result.error("Category with ID: " + id + " not found."));
            }
        } catch (Exception e) {
            return CompletableFuture.completedFuture(Result.error("Failed to delete subcategory: " + e.getMessage()));
        }
    }
}
