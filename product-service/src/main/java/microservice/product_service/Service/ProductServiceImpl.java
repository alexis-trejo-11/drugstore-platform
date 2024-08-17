package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.product_service.Mappers.ProductMapper;
import microservice.product_service.Model.*;
import microservice.product_service.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
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

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<ProductDTO>> getAllProducts() {
        return CompletableFuture.supplyAsync(() -> {
            List<Product> products = productRepository.findAll();
            return products.stream()
                    .map(productMapper::productToDTO)
                    .collect(Collectors.toList());
        });
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<ProductDTO>> getProductsById(List<Long> productIds) {
        return CompletableFuture.supplyAsync(() -> {
            List<Product> products = productRepository.findByIdIn(productIds);
            return products.stream()
                    .map(productMapper::productToDTO)
                    .collect(Collectors.toList());
        });
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<ProductDTO> getProductById(Long productId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Product> productOptional = productRepository.findById(productId);
            return productOptional.map(productMapper::productToDTO).orElse(null);
        });
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<ProductDTO>> findProductsBySupplier(Long supplierId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Product> products = productRepository.findBySupplier_Id(supplierId);
            return products.stream()
                    .map(productMapper::productToDTO)
                    .collect(Collectors.toList());
        });
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<ProductDTO>> findProductsByCategoryId(Long categoryId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Product> products = productRepository.findByCategory_Id(categoryId);
            return products.stream()
                    .map(productMapper::productToDTO)
                    .collect(Collectors.toList());
        });
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<ProductDTO>> findProductsBySubCategory(Long subcategoryId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Product> products = productRepository.findBySubcategory_Id(subcategoryId);
            return products.stream()
                    .map(productMapper::productToDTO)
                    .collect(Collectors.toList());
        });
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> processInsertProduct(ProductInsertDTO productInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            Product product = productMapper.insertDtoToProduct(productInsertDTO);

            // Validate And Set Relationship Values In Model Created
            /*
            Result<Void> relationshipResult = handleRelationShips(productInsertDTO, product);
            if (!relationshipResult.isSuccess()) {
                return Result.error(relationshipResult.getErrorMessage());
            }
             */

            addProduct(productInsertDTO);
            return Result.success();
        });
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> updateProduct(Long productId, ProductInsertDTO productInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (optionalProduct.isEmpty()) {
                return Result.error("Product Not Found");
            }

            Long productFoundedId = optionalProduct.get().getId();
            Product product = productMapper.insertDtoToProduct(productInsertDTO);
            product.setUpdatedAt(LocalDateTime.now());
            product.setId(productFoundedId);

            Result<Void> relationshipResult = handleRelationShips(productInsertDTO, product);
            if (!relationshipResult.isSuccess()) {
                return Result.error(relationshipResult.getErrorMessage());
            }

            productRepository.saveAndFlush(product);
            return Result.success();
        });
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Boolean> deleteProduct(Long productId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (optionalProduct.isEmpty()) {
                return false;
            }

            productRepository.deleteById(productId);
            return true;
        });
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

    private void addRelationship(Product product, MainCategory mainCategory, Category category, Subcategory subcategory, Supplier supplier, Brand brand) {
        product.setMainCategory(mainCategory);
        product.setCategory(category);
        product.setSubcategory(subcategory);
        product.setSupplier(supplier);
        product.setBrand(brand);
    }
}
