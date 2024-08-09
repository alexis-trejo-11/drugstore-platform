package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Product.Category.SubcategoryDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Product.Category.CategoryDTO;
import microservice.product_service.Mappers.CategoryMapper;
import microservice.product_service.Mappers.ProductMapper;
import microservice.product_service.Mappers.SubCategoryMapper;
import microservice.product_service.Model.Category;
import microservice.product_service.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final SubCategoryMapper subCategoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, ProductMapper productMapper, SubCategoryMapper subCategoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
        this.subCategoryMapper = subCategoryMapper;
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

            CategoryDTO categoryDTO = categoryMapper.categoryToDTO(category.get());
            List<ProductDTO> productInsertDTOS = category.get().getProducts().stream()
                    .map(productMapper::productToDTO)
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

            CategoryDTO categoryDTO = categoryMapper.categoryToDTO(category.get());

            List<SubcategoryDTO> subcategoryDTOS = category.get().getSubCategories().stream()
                    .map(subCategoryMapper::subcategoryToDTO)
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

            CategoryDTO categoryDTO = categoryMapper.categoryToDTO(category.get());

            List<ProductDTO> productDTOS = category.get().getProducts().stream()
                    .map(productMapper::productToDTO)
                    .collect(Collectors.toList());
            categoryDTO.setProductsDTO(productDTOS);

            List<SubcategoryDTO> subcategoryDTOS = category.get().getSubCategories().stream()
                    .map(subCategoryMapper::subcategoryToDTO)
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
