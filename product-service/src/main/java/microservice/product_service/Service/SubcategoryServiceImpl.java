package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.PaginatedResponseDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubCategoryInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubCategoryUpdateDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Category.SubcategoryDTO;
import microservice.product_service.Mappers.ProductMapper;
import microservice.product_service.Mappers.SubCategoryMapper;
import microservice.product_service.Model.Product;
import microservice.product_service.Model.Subcategory;
import microservice.product_service.Repository.ProductRepository;
import microservice.product_service.Repository.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SubcategoryServiceImpl implements SubcategoryService {

    private final SubcategoryRepository subcategoryRepository;
    private final SubCategoryMapper subCategoryMapper;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    @Autowired
    public SubcategoryServiceImpl(SubcategoryRepository subcategoryRepository,
                                  SubCategoryMapper subCategoryMapper, ProductMapper productMapper,
                                  ProductRepository productRepository) {
        this.subcategoryRepository = subcategoryRepository;
        this.subCategoryMapper = subCategoryMapper;
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    @Override
    @Cacheable(value = "allSubcategories")
    public Page<SubcategoryDTO> getAllSubCategories(Pageable pageable) {
        Page<Subcategory> subcategoryPage = subcategoryRepository.findAll(pageable);
        return subcategoryPage.map(subCategoryMapper::subcategoryToDTO);
    }

    @Override
    @Cacheable(value = "subcategoryWithProducts", key = "#subcategoryId")
    public SubcategoryDTO getSubCategoryByIdWithProducts(Long subcategoryId, Pageable pageable) {
        Optional<Subcategory> optionalSubcategory = subcategoryRepository.findById(subcategoryId);
        if (optionalSubcategory.isEmpty()) {
            throw new RuntimeException();
        }
        Subcategory subcategory = optionalSubcategory.get();

        SubcategoryDTO subcategoryReturnDTO = subCategoryMapper.subcategoryToDTO(subcategory);
        Page<Product> productPage = productRepository.findBySubcategory(subcategory, pageable);

        List<ProductDTO> productDTOs = productPage.map(productMapper::productToDTO).stream().toList();
        PaginatedResponseDTO<ProductDTO> paginatedProductDTO = new PaginatedResponseDTO<>(
                productDTOs, pageable.getPageNumber(), pageable.getPageSize(), productPage.getTotalElements()
        );

        subcategoryReturnDTO.setProductDTOS(paginatedProductDTO);

        return subcategoryReturnDTO;
    }

    @Override
    @Transactional
    public void insertCategory(SubCategoryInsertDTO subCategoryInsertDTO) {
        Subcategory subcategory = new Subcategory(subCategoryInsertDTO);
        subcategoryRepository.saveAndFlush(subcategory);
    }

    @Override
    @Transactional
    public void updateCategory(SubCategoryUpdateDTO subCategoryUpdateDTO) {
        Optional<Subcategory> optionalSubcategory = subcategoryRepository.findById(subCategoryUpdateDTO.getId());
        if (optionalSubcategory.isEmpty()) {
            throw new RuntimeException();
        }

        Subcategory subcategory = optionalSubcategory.get();
        subcategory.updateFromDTO(subCategoryUpdateDTO);

        subcategoryRepository.saveAndFlush(subcategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long subcategoryID) {
        Optional<Subcategory> optionalSubcategory = subcategoryRepository.findById(subcategoryID);
        if (optionalSubcategory.isEmpty()) {
            throw new RuntimeException();
        }

        subcategoryRepository.deleteById(subcategoryID);
    }
}

