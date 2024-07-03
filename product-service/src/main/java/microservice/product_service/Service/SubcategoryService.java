package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.Category.SubcategoryDTO;
import microservice.product_service.Mappers.SubCategoryMapper;
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
    private final SubCategoryMapper subCategoryMapper;

    @Autowired
    public SubcategoryService(SubcategoryRepository subcategoryRepository, SubCategoryMapper subCategoryMapper) {
        this.subcategoryRepository = subcategoryRepository;
        this.subCategoryMapper = subCategoryMapper;
    }

    @Async
    @Transactional
    public void insertCategory(SubcategoryDTO subcategoryDTO) {
        Subcategory subcategory = new Subcategory(subcategoryDTO);
        subcategoryRepository.saveAndFlush(subcategory);
    }

    @Async
    @Transactional
    public List<SubcategoryDTO> findAllSubCategories() {
        List<Subcategory> subcategories = subcategoryRepository.findAll();
        return subcategories.stream()
                .map(subCategoryMapper::subcategoryToDTO)
                .collect(Collectors.toList());
    }

    @Async
    @Transactional
    public SubcategoryDTO findSubCategoryByIdWithProducts(Long id) {
        Optional<Subcategory> subcategoryOpt = subcategoryRepository.findById(id);
        if (subcategoryOpt.isEmpty()) {
            return null;
        }

        Subcategory subcategory = subcategoryOpt.get();
        SubcategoryDTO subcategoryReturnDTO = subCategoryMapper.subcategoryToDTO(subcategory);

        List<ProductDTO> productDTOs = subcategory.getProducts().stream()
                .map(ModelTransformer::productToDTO)
                .collect(Collectors.toList());
        subcategoryReturnDTO.setProductInsertDTOS(productDTOs);

        return subcategoryReturnDTO;
    }

    @Async
    @Transactional
    public SubcategoryDTO updateCategory(Long id, SubcategoryDTO subcategoryDTO) {
        Optional<Subcategory> optionalSubcategory = subcategoryRepository.findById(id);
        if (optionalSubcategory.isEmpty()) {
            return null;
        }

        Subcategory subcategory = optionalSubcategory.get();
        subcategory.updateFromDTO(subcategoryDTO);

        subcategoryRepository.saveAndFlush(subcategory);

        return subCategoryMapper.subcategoryToDTO(subcategory);
    }

    @Async
    @Transactional
    public SubcategoryDTO deleteCategory(Long id) {
        Optional<Subcategory> optionalSubcategory = subcategoryRepository.findById(id);
        if (optionalSubcategory.isEmpty()) {
            return null;
        }

        Subcategory subcategory = optionalSubcategory.get();
        SubcategoryDTO subcategoryReturnDTO = subCategoryMapper.subcategoryToDTO(subcategory);
        subcategoryRepository.delete(subcategory);

        return subcategoryReturnDTO;
    }
}
