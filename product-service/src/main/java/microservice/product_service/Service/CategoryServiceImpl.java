package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.CategoryInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.CategoryUpdateDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubcategoryDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.CategoryDTO;
import at.backend.drugstore.microservice.common_classes.Utils.EntityMapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.product_service.Mappers.CategoryMapper;
import microservice.product_service.Mappers.ProductMapper;
import microservice.product_service.Mappers.SubCategoryMapper;
import microservice.product_service.Model.Category;
import microservice.product_service.Model.MainCategory;
import microservice.product_service.Repository.CategoryRepository;
import microservice.product_service.Repository.MainCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final SubCategoryMapper subCategoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, MainCategoryRepository mainCategoryRepository,
                               CategoryMapper categoryMapper,
                               ProductMapper productMapper,
                               SubCategoryMapper subCategoryMapper) {
        this.categoryRepository = categoryRepository;
        this.mainCategoryRepository = mainCategoryRepository;
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
        this.subCategoryMapper = subCategoryMapper;
    }

    @Override
    @Cacheable(value = "categoryWithProducts", key = "#categoryID + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<CategoryDTO>  findCategoryByIdWithProducts(Long categoryID, Pageable pageable) {
        Page<Category>  optionalCategory = categoryRepository.findById(categoryID, pageable);

        return optionalCategory.map(category -> {
            CategoryDTO categoryDTO = categoryMapper.categoryToDTO(category);

            // Map the Products of the Category to ProductDTOs
            List<ProductDTO> productDTOS = category.getProducts().stream()
                    .map(productMapper::productToDTO)
                    .collect(Collectors.toList());
            categoryDTO.setProductsDTO(productDTOS);

            return categoryDTO;
        });
    }

    @Override
    @Cacheable(value = "categoryWithSubcategories", key = "#categoryID + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<CategoryDTO> findCategoryByIdWithSubcategory(Long categoryID, Pageable pageable) {
            Page<Category> optionalCategory = categoryRepository.findById(categoryID, pageable);

            return optionalCategory.map(category -> {
                CategoryDTO categoryDTO = categoryMapper.categoryToDTO(category);

                // Map the Subcategory of the Category to SubcategoryDTOs
                List<SubcategoryDTO> subcategoryDTOS = category.getSubCategories().stream()
                        .map(subCategoryMapper::subcategoryToDTO)
                        .collect(Collectors.toList());
                categoryDTO.setSubcategoriesDTOS(subcategoryDTOS);

                return categoryDTO;
            });
    }

    @Override
    @Cacheable(value = "categoryWithSubcategoriesAndProducts", key = "#categoryID + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<CategoryDTO> findCategoryByIdWithSubCategoriesAndProducts(Long categoryID, Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findById(categoryID, pageable);

        return categoryPage.map(category -> {
            CategoryDTO categoryDTO = categoryMapper.categoryToDTO(category);

            // Map the Products of the Category to ProductDTOs
            List<ProductDTO> productDTOS = category.getProducts().stream()
                    .map(productMapper::productToDTO)
                    .collect(Collectors.toList());
            categoryDTO.setProductsDTO(productDTOS);

            // Map the Subcategories of the Category to SubcategoryDTOs
            List<SubcategoryDTO> subcategoryDTOS = category.getSubCategories().stream()
                    .map(subCategoryMapper::subcategoryToDTO)
                    .collect(Collectors.toList());
            categoryDTO.setSubcategoriesDTOS(subcategoryDTOS);

            return categoryDTO;
        });
    }


    @Override
    @Transactional
    public Result<Void> insertCategory(CategoryInsertDTO categoryInsertDTO) {
        Optional<MainCategory> optionalMainCategory = mainCategoryRepository.findById(categoryInsertDTO.getMainCategoryID());
        if (optionalMainCategory.isEmpty()) {
            return Result.error("Main Category With ID " + categoryInsertDTO.getMainCategoryID() + " Not Found");
        }

        Category category = new Category(categoryInsertDTO.getName(), optionalMainCategory.get());
        categoryRepository.saveAndFlush(category);

        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> updateCategory(CategoryUpdateDTO categoryUpdateDTO) {
        Optional<MainCategory> optionalMainCategory = mainCategoryRepository.findById(categoryUpdateDTO.getMainCategoryID());
        if (optionalMainCategory.isEmpty()) {
            return Result.error("Main Category With ID " + categoryUpdateDTO.getMainCategoryID() + " Not Found");
        }

        Category category = categoryRepository.findById(categoryUpdateDTO.getId()).orElseThrow(() -> new RuntimeException("MainCategory not found"));
        category.setName(categoryUpdateDTO.getName());
        category.setMainCategory(optionalMainCategory.get());

        categoryRepository.saveAndFlush(category);

        return Result.success();
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
            categoryRepository.deleteById(categoryId);
    }

    @Override
    public boolean validateExistingCategory(Long categoryID) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryID);
        return optionalCategory.isPresent();
    }

}
