package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductRelationsIDs;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.EntityMapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.product_service.Mappers.ProductMapper;
import microservice.product_service.Model.*;
import org.springframework.cache.annotation.Cacheable;
import microservice.product_service.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final SupplierRepository supplierRepository;

    @Autowired
    public ProductServiceImpl(BrandRepository brandRepository,
                              CategoryRepository categoryRepository,
                              MainCategoryRepository mainCategoryRepository,
                              ProductMapper productMapper,
                              ProductRepository productRepository,
                              SubcategoryRepository subcategoryRepository,
                              SupplierRepository supplierRepository) {
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.mainCategoryRepository = mainCategoryRepository;
        this.productMapper = productMapper;
        this.productRepository = productRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.supplierRepository = supplierRepository;
    }

    @Async("taskExecutor")
    @Cacheable(value = "productsByIds", key = "#productIds")
    public CompletableFuture<List<ProductDTO>> getProductsById(List<Long> productIds) {
        return CompletableFuture.supplyAsync(() -> {
            List<Product> products = productRepository.findByIdIn(productIds);
            return products.stream()
                    .map(productMapper::productToDTO)
                    .collect(Collectors.toList());
        });
    }

    @Async("taskExecutor")
    @Cacheable(value = "productById", key = "#productId")
    public CompletableFuture<ProductDTO> getProductById(Long productId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Product> productOptional = productRepository.findById(productId);
            return productOptional.map(productMapper::productToDTO).orElse(null);
        });
    }

    @Transactional
    @Cacheable(value = "allProductsSortedByCategory", key = "'page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize")
    public Page<ProductDTO> getAllProductsSortedByCategoryHierarchy(Pageable pageable) {
        Page<Product> products = productRepository.findAllSortedByCategoryHierarchy(pageable);
        return products.map(productMapper::productToDTO);
    }

    @Transactional
    @Cacheable(value = "productsBySupplier", key = "#supplierId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductDTO> getProductsBySupplier(Long supplierId, Pageable pageable) {
            Page<Product> products = productRepository.findBySupplier_Id(supplierId, pageable);
            return products.map(productMapper::productToDTO);
    }

    @Transactional
    @Cacheable(value = "productsByCategory", key = "#categoryId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductDTO> getProductsByCategoryId(Long categoryId, Pageable pageable) {
            Page<Product> products = productRepository.findByCategory_Id(categoryId, pageable);
            return products.map(productMapper::productToDTO);
    }

    @Transactional
    @Cacheable(value = "productsBySubcategory", key = "#subcategoryId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductDTO> getProductsBySubCategory(Long subcategoryId, Pageable pageable) {
        Page<Product> products = productRepository.findBySubcategory_Id(subcategoryId, pageable);
        return products.map(productMapper::productToDTO);
    }

    @Transactional
    public Result<Void> createProduct(ProductInsertDTO productInsertDTO) {
            Product product = productMapper.insertDtoToProduct(productInsertDTO);

            // Validate And Set Relationship Values In Model Created
            Result<Void> relationshipResult = handleRelationShips(productInsertDTO.getRelationIDs(), product);
            if (!relationshipResult.isSuccess()) {
                return Result.error(relationshipResult.getErrorMessage());
            }

            addProduct(productInsertDTO);
            return Result.success();
    }

    @Transactional
    public Result<Void> updateProduct(ProductUpdateDTO productUpdateDTO) {
        Optional<Product> optionalProduct = productRepository.findById(productUpdateDTO.getId());
        Product product = optionalProduct.get();

        // Map DTO and assign not null values to product
        EntityMapper.mapNonNullProperties(productUpdateDTO, product);

        Result<Void> relationshipResult = handleRelationShips(productUpdateDTO.getRelationIDs(), product);
            if (!relationshipResult.isSuccess()) {
                return Result.error(relationshipResult.getErrorMessage());
            }
            productRepository.saveAndFlush(product);

            return Result.success();
    }

    @Transactional
    public void deleteProduct(Long productId) {
            productRepository.deleteById(productId);
    }

    @Override
    public boolean validateExisitingProduct(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        return optionalProduct.isEmpty();
    }


    private Result<Void> handleRelationShips(ProductRelationsIDs productRelationsIDs, Product product) {
        Optional<MainCategory> optionalMainCategory = mainCategoryRepository.findById(productRelationsIDs.getMainCategoryId());
        if (optionalMainCategory.isEmpty()) {
            return Result.error("Invalid Main Category");
        }

        Optional<Category> optionalCategory = categoryRepository.findById(productRelationsIDs.getCategoryId());
        if (optionalCategory.isEmpty()) {
            return Result.error("Invalid Category");
        }

        Optional<Subcategory> optionalSubcategory = subcategoryRepository.findById(productRelationsIDs.getSubcategoryId());
        if (optionalSubcategory.isEmpty()) {
            return Result.error("Invalid SubCategory");
        }

        Optional<Brand> optionalBrand = brandRepository.findById(productRelationsIDs.getBrandId());
        if (optionalBrand.isEmpty()) {
            return Result.error("Invalid Brand");
        }

        Optional<Supplier> optionalSupplier = supplierRepository.findById(productRelationsIDs.getSupplierId());
        if (optionalSupplier.isEmpty()) {
            return Result.error("Invalid Supplier");
        }

        addRelationship(product, optionalMainCategory.get(), optionalCategory.get(), optionalSubcategory.get(), optionalSupplier.get(), optionalBrand.get());
        return Result.success();
    }

    private void addProduct(ProductInsertDTO productInsertDTO) {
        Product product = productMapper.insertDtoToProduct(productInsertDTO);
        productRepository.saveAndFlush(product);
    }

    private void addRelationship(Product product, MainCategory mainCategory, Category category, Subcategory subcategory, Supplier supplier, Brand brand) {
        product.setMainCategory(mainCategory);
        product.setCategory(category);
        product.setSubcategory(subcategory);
        product.setSupplier(supplier);
        product.setBrand(brand);
    }
}
