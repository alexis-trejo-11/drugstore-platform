package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.PaginatedResponseDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.*;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import com.sun.tools.javac.Main;
import microservice.product_service.Mappers.CategoryMapper;
import microservice.product_service.Mappers.MainCategoryMapper;
import microservice.product_service.Mappers.SubCategoryMapper;
import microservice.product_service.Model.Category;
import microservice.product_service.Model.MainCategory;
import microservice.product_service.Model.Product;
import microservice.product_service.Model.Subcategory;
import microservice.product_service.Repository.CategoryRepository;
import microservice.product_service.Repository.MainCategoryRepository;
import microservice.product_service.Repository.ProductRepository;
import microservice.product_service.Repository.SubcategoryRepository;
import microservice.product_service.Utils.ModelTransformer;
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
public class MainCategoryServiceImpl implements MainCategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final MainCategoryMapper mainCategoryMapper;
    private final MainCategoryRepository mainCategoryRepository;
    private final ProductRepository productRepository;
    private final SubCategoryMapper subCategoryMapper;
    private final SubcategoryRepository subcategoryRepository;

    @Autowired
    public MainCategoryServiceImpl(CategoryMapper categoryMapper,
                                   CategoryRepository categoryRepository,
                                   MainCategoryMapper mainCategoryMapper,
                                   MainCategoryRepository mainCategoryRepository,
                                   ProductRepository productRepository,
                                   SubCategoryMapper subCategoryMapper,
                                   SubcategoryRepository subcategoryRepository) {
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
        this.mainCategoryMapper = mainCategoryMapper;
        this.mainCategoryRepository = mainCategoryRepository;
        this.productRepository = productRepository;
        this.subCategoryMapper = subCategoryMapper;
        this.subcategoryRepository = subcategoryRepository;
    }


    @Override
    @Cacheable(value = "mainCategoryWithCategoriesAndSubcategories", key = "#mainCategoryID + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public MainCategoryDTO getMainCategoryByIdWithCategoryAndSubCategory(Long mainCategoryID, Pageable pageable) {
        Optional<MainCategory> optionalMainCategory = mainCategoryRepository.findById(mainCategoryID);
        if (optionalMainCategory.isEmpty()) {
            throw new RuntimeException();
        }

        MainCategory mainCategory = optionalMainCategory.get();
        MainCategoryDTO mainCategoryDTO = mainCategoryMapper.mainCategoryToDTO(mainCategory);

        // Fetch and map categories with pagination
        Page<Category> categoryPage = categoryRepository.findByMainCategory(mainCategory, pageable);
        List<CategoryDTO> categoryDTOS = categoryPage.getContent().stream()
                .map(categoryMapper::categoryToDTO)
                .collect(Collectors.toList());
        PaginatedResponseDTO<CategoryDTO> paginatedCategories = new PaginatedResponseDTO<>(
                categoryDTOS, pageable.getPageNumber(), pageable.getPageSize(), categoryPage.getTotalElements()
        );
        mainCategoryDTO.setCategoryDTOS(paginatedCategories);

        // Fetch and map subcategories with pagination
        Page<Subcategory> subcategoryPage = subcategoryRepository.findByMainCategory(mainCategory, pageable);
        List<SubcategoryDTO> subcategoryDTOS = subcategoryPage.getContent().stream()
                .map(subCategoryMapper::subcategoryToDTO)
                .collect(Collectors.toList());
        PaginatedResponseDTO<SubcategoryDTO> paginatedSubcategories = new PaginatedResponseDTO<>(
                subcategoryDTOS, pageable.getPageNumber(), pageable.getPageSize(), subcategoryPage.getTotalElements()
        );
        mainCategoryDTO.setSubcategoriesDTOS(paginatedSubcategories);

        return mainCategoryDTO;
    }


    @Override
    @Cacheable(value = "mainCategoryWithProducts", key = "#mainCategoryID + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public MainCategoryDTO getMainCategoryByIdWithProducts(Long mainCategoryID, Pageable pageable) {
        Optional<MainCategory> optionalMainCategory = mainCategoryRepository.findById(mainCategoryID);
        if (optionalMainCategory.isEmpty()) {
            throw new RuntimeException("MainCategory not found");
        }

        MainCategory mainCategory = optionalMainCategory.get();
        MainCategoryDTO mainCategoryDTO = mainCategoryMapper.mainCategoryToDTO(mainCategory);

        // Bring and Fetch Pageable Data
        Page<Product> productPage = productRepository.findByMainCategory(mainCategory, pageable);
        List<ProductDTO> productDTOS = productPage.getContent().stream()
                .map(ModelTransformer::productToDTO)
                .collect(Collectors.toList());
        PaginatedResponseDTO<ProductDTO> paginatedSubcategories = new PaginatedResponseDTO<>(
                productDTOS, pageable.getPageNumber(), pageable.getPageSize(), productPage.getTotalElements()
        );
        mainCategoryDTO.setProductsDTO(paginatedSubcategories);

        return mainCategoryDTO;
    }


    @Override
    @Transactional
    public void insertCategory(MainCategoryInsertDTO mainCategoryInsertDTO) {
        MainCategory mainCategory = new MainCategory(mainCategoryInsertDTO.getName(), LocalDateTime.now(), LocalDateTime.now());
        mainCategoryRepository.saveAndFlush(mainCategory);
    }


    @Override
    @Transactional
    public void updateMainCategory(MainCategoryUpdateDTO mainCategoryUpdateDTO) {
            Optional<MainCategory> optionalMainCategory = mainCategoryRepository.findById(mainCategoryUpdateDTO.getId());
            if (optionalMainCategory.isEmpty()) {
                throw new RuntimeException();
            }

            MainCategory mainCategory = new MainCategory();
            mainCategory.setName(mainCategoryUpdateDTO.getName());
            mainCategory.setUpdatedAt(LocalDateTime.now());

            mainCategoryRepository.saveAndFlush(mainCategory);
    }

    @Override
    @Transactional
    public void deleteMainCategoryById(Long mainCategoryID) {
            Optional<MainCategory> optionalMainCategory = mainCategoryRepository.findById(mainCategoryID);
            if (optionalMainCategory.isEmpty()) {
                throw new RuntimeException();
            }

            mainCategoryRepository.deleteById(mainCategoryID);
    }

}