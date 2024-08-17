package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubcategoryDTO;
import microservice.product_service.Mappers.SubCategoryMapper;
import microservice.product_service.Model.Subcategory;
import microservice.product_service.Repository.SubcategoryRepository;
import microservice.product_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubcategoryServiceImpl implements SubcategoryService {

    private final SubcategoryRepository subcategoryRepository;
    private final SubCategoryMapper subCategoryMapper;

    @Autowired
    public SubcategoryServiceImpl(SubcategoryRepository subcategoryRepository, SubCategoryMapper subCategoryMapper) {
        this.subcategoryRepository = subcategoryRepository;
        this.subCategoryMapper = subCategoryMapper;
    }

    @Override
    @Async
    @Transactional
    public void insertCategory(SubcategoryDTO subcategoryDTO) {
        Subcategory subcategory = new Subcategory(subcategoryDTO);
        subcategoryRepository.saveAndFlush(subcategory);
    }

    @Override
    @Async
    @Transactional
    public List<SubcategoryDTO> findAllSubCategories() {
        List<Subcategory> subcategories = subcategoryRepository.findAll();
        return subcategories.stream()
                .map(subCategoryMapper::subcategoryToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Async
    @Transactional
    public SubcategoryDTO findSubCategoryByIdWithProducts(Long subcategoryId) {
        Optional<Subcategory> subcategoryOpt = subcategoryRepository.findById(subcategoryId);
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

    @Override
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

    @Override
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
