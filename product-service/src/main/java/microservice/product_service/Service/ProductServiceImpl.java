package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.product_service.Mappers.ProductMapper;
import microservice.product_service.Model.*;
import microservice.product_service.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final SupplierRepository supplierRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              MainCategoryRepository mainCategoryRepository,
                              CategoryRepository categoryRepository,
                              SubcategoryRepository subcategoryRepository,
                              SupplierRepository supplierRepository,
                              BrandRepository brandRepository,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.mainCategoryRepository = mainCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.supplierRepository = supplierRepository;
        this.brandRepository = brandRepository;
        this.productMapper = productMapper;
    }

    @Async
    @Transactional
    public CompletableFuture<List<ProductDTO>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = products.stream()
                .map(productMapper::productToDTO)
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(productDTOs);
    }

    @Async
    @Transactional
    public CompletableFuture<List<ProductDTO>> getProductsById(List<Long> productId) {
        List<Product> products = productRepository.findByIdIn(productId);
        List<ProductDTO> productDTOs = products.stream()
                .map(productMapper::productToDTO)
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(productDTOs);
    }

    @Async
    @Transactional
    public CompletableFuture<ProductDTO> getProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        Product product = productOptional.get();
        return CompletableFuture.completedFuture(productMapper.productToDTO(product));
    }

    @Async
    @Transactional
    public CompletableFuture<List<ProductDTO>> findProductsBySupplier(Long supplierId) {
        List<Product> products = productRepository.findBySupplier_Id(supplierId);
        List<ProductDTO> productDTOs = products.stream()
                .map(productMapper::productToDTO)
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(productDTOs);
    }

    @Async
    @Transactional
    public CompletableFuture<List<ProductDTO>> findProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findByCategory_Id(categoryId);
        List<ProductDTO> productDTOs = products.stream()
                .map(productMapper::productToDTO)
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(productDTOs);
    }

    @Async
    @Transactional
    public CompletableFuture<List<ProductDTO>> findProductsBySubCategory(Long subcategoryId) {
        List<Product> products = productRepository.findBySubcategory_Id(subcategoryId);
        List<ProductDTO> productDTOs = products.stream()
                .map(productMapper::productToDTO)
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(productDTOs);
    }

    @Async
    @Transactional
    public CompletableFuture<Result<Void>> processInsertProduct(ProductInsertDTO productInsertDTO) {
        Product product = productMapper.insertDtoToProduct(productInsertDTO);

        // Validate And Set Relationship Values In Model Created
        Result<Void> relationshipResult = handleRelationShips(productInsertDTO, product);
        if (!relationshipResult.isSuccess()) {
            return CompletableFuture.completedFuture(Result.error(relationshipResult.getErrorMessage()));
        }

        addProduct(productInsertDTO);
        return CompletableFuture.completedFuture(Result.success());
    }

    @Async
    @Transactional
    public CompletableFuture<Result<Void>> updateProduct(Long productId, ProductInsertDTO productInsertDTO) {
        try {
            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (optionalProduct.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Product Not Found"));
            }

            Long productFoundedId = optionalProduct.get().getId();
            Product product = productMapper.insertDtoToProduct(productInsertDTO);
            product.setUpdatedAt(LocalDateTime.now());
            product.setId(productFoundedId);

            Result<Void> relationshipResult = handleRelationShips(productInsertDTO, product);
            if (!relationshipResult.isSuccess()) {
                return CompletableFuture.completedFuture(Result.error(relationshipResult.getErrorMessage()));
            }

            productRepository.saveAndFlush(product);
            return CompletableFuture.completedFuture(Result.success());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Boolean> deleteProduct(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }

        productRepository.deleteById(productId);
        return CompletableFuture.completedFuture(true);

    }

    private Result<Void> handleRelationShips(ProductInsertDTO productInsertDTO, Product product) {
        Optional<MainCategory> optionalMainCategory = mainCategoryRepository.findById(productInsertDTO.getMainCategoryId());
        if (optionalMainCategory.isEmpty()) {
            return Result.error("Invalid Main Category");
        }

        Optional<Category> optionalCategory = categoryRepository.findById(productInsertDTO.getCategoryId());
        if (optionalCategory.isEmpty()) {
            return Result.error("Invalid Category");
        }

        Optional<Subcategory> optionalSubcategory = subcategoryRepository.findById(productInsertDTO.getSubcategoryId());
        if (optionalSubcategory.isEmpty()) {
            return Result.error("Invalid SubCategory");
        }

        Optional<Brand> optionalBrand = brandRepository.findById(productInsertDTO.getBrandId());
        if (optionalBrand.isEmpty()) {
            return Result.error("Invalid Brand");
        }

        Optional<Supplier> optionalSupplier = supplierRepository.findById(productInsertDTO.getSupplierId());
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

    private void addRelationship(Product product , MainCategory mainCategory, Category category, Subcategory subcategory, Supplier supplier, Brand brand) {
        product.setMainCategory(mainCategory);
        product.setCategory(category);
        product.setSubcategory(subcategory);
        product.setSupplier(supplier);
        product.setBrand(brand);
    }

}


