package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.Category.SubcategoryReturnDTO;
import microservice.product_service.Model.Subcategory;
import microservice.product_service.Repository.SubcategoryRepository;
import microservice.product_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubcategoryService {

    private final SubcategoryRepository subcategoryRepository;

    @Autowired
    public SubcategoryService(SubcategoryRepository subcategoryRepository) {
        this.subcategoryRepository = subcategoryRepository;
    }

    @Async
    @Transactional
    public void insertCategory(SubcategoryReturnDTO subcategoryDTO) {
        try {
            Subcategory subcategory = new Subcategory(subcategoryDTO);

            subcategoryRepository.saveAndFlush(subcategory);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public List<SubcategoryReturnDTO> findAllSubCategories() {
        try {
            List<Subcategory> subcategories = subcategoryRepository.findAll();
            return subcategories.stream()
                    .map(ModelTransformer::subcategoryToReturnDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public SubcategoryReturnDTO findSubCategoryByIdWithProducts(Long id) {
        try {
            Optional<Subcategory> subcategoryOpt = subcategoryRepository.findById(id);
            if (subcategoryOpt.isEmpty()) {
               return null;
            }

            Subcategory subcategory = subcategoryOpt.get();
            SubcategoryReturnDTO subcategoryReturnDTO = ModelTransformer.subcategoryToReturnDTO(subcategory);

            List<ProductDTO> productDTOs = subcategory.getProducts().stream()
                    .map(ModelTransformer::productToDTO)
                    .collect(Collectors.toList());
            subcategoryReturnDTO.setProductInsertDTOS(productDTOs);

            return subcategoryReturnDTO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public SubcategoryReturnDTO updateCategory(Long id, SubcategoryReturnDTO subcategoryDTO) {
        try {
            Optional<Subcategory> optionalSubcategory = subcategoryRepository.findById(id);
            if (optionalSubcategory.isEmpty()) {
                return null;
            }

            Subcategory subcategory = optionalSubcategory.get();
            subcategory.updateFromDTO(subcategoryDTO);
            subcategoryRepository.saveAndFlush(subcategory);

            return ModelTransformer.subcategoryToReturnDTO(subcategory);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public SubcategoryReturnDTO deleteCategory(Long id) {
        try {
            Optional<Subcategory> optionalSubcategory = subcategoryRepository.findById(id);
            if (optionalSubcategory.isEmpty()) {
                return null;
            }

            Subcategory subcategory = optionalSubcategory.get();
            SubcategoryReturnDTO subcategoryReturnDTO = ModelTransformer.subcategoryToReturnDTO(subcategory);
            subcategoryRepository.delete(subcategory);

            return subcategoryReturnDTO;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
