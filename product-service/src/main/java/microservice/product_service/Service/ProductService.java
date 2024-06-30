package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import microservice.product_service.Model.*;
import microservice.product_service.Repository.*;
import microservice.product_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final SupplierRepository supplierRepository;
    private final BrandRepository brandRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, MainCategoryRepository mainCategoryRepository, CategoryRepository categoryRepository, SubcategoryRepository subcategoryRepository, SupplierRepository supplierRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.mainCategoryRepository = mainCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.supplierRepository = supplierRepository;
        this.brandRepository = brandRepository;
    }


    @Transactional
    public CompletableFuture<List<ProductDTO>> getAll() {
        try {
            List<Product> products = productRepository.findAll();

            List<ProductDTO> productDTOS = products.stream()
                    .map(ModelTransformer::productToDTO)
                    .collect(Collectors.toList());

            return CompletableFuture.completedFuture(productDTOS);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException("An Error Occurred While Getting Products", e));
        }
    }

    public CompletableFuture<Result<List<ProductDTO>>> getProductsById(List<Long> productId) {
        try {
            List<Product> products = productRepository.findByIdIn(productId);

            // Check if all requested products were found
            Set<Long> foundProductIds = products.stream()
                    .map(Product::getId)
                    .collect(Collectors.toSet());
            Set<Long> missingProductIds = new HashSet<>(productId);
            missingProductIds.removeAll(foundProductIds);

            if (!missingProductIds.isEmpty()) {
                // Handle the case where some products were not found
                Result errorResult = new Result<>();
                errorResult.setErrorMessage("Products not found for IDs: " + missingProductIds);
                return CompletableFuture.completedFuture(errorResult);
            }

            List<ProductDTO> productDTOS = products.stream()
                    .map(ModelTransformer::productToDTO)
                    .collect(Collectors.toList());

            return CompletableFuture.completedFuture(Result.success(productDTOS));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException(e.getMessage()));
        }
    }

    @Transactional
    public CompletableFuture<Result<ProductDTO>> getProductById(Long productId) {
        try {
            Optional<Product> productOptional = productRepository.findById(productId);
            if(productOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Product With Id:"+ productId + " Not Found"));
            }

            Product product = productOptional.get();
            ProductDTO productDTO = ModelTransformer.productToDTO(product);

            return CompletableFuture.completedFuture(Result.success(productDTO));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException(e.getMessage()));
        }
    }

    @Transactional
    public CompletableFuture<List<ProductDTO>> FindProductsBySupplier(Long supplierId) {
        try {
            List<Product> products = productRepository.findBySupplier_Id(supplierId);

            List<ProductDTO> productDTOS = products.stream()
                    .map(ModelTransformer::productToDTO)
                    .collect(Collectors.toList());

            return CompletableFuture.completedFuture(productDTOS);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException(e.getMessage()));
        }
    }

    @Transactional
    public CompletableFuture<List<ProductDTO>> findProductsByCategoryId(Long categoryId) {
        try {
            List<Product> products = productRepository.findByCategory_Id(categoryId);

            List<ProductDTO> productDTOS = products.stream()
                    .map(ModelTransformer::productToDTO)
                    .collect(Collectors.toList());

            return CompletableFuture.completedFuture(productDTOS);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException(e.getMessage()));
        }
    }

    @Transactional
    public CompletableFuture<List<ProductDTO>> findProductsBySubCategory(Long subcategoryId) {
        try {
            List<Product> products = productRepository.findBySubcategory_Id(subcategoryId);
            if (products.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }

            List<ProductDTO> productDTOS = products.stream()
                    .map(ModelTransformer::productToDTO)
                    .collect(Collectors.toList());

            return CompletableFuture.completedFuture(productDTOS);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException(e.getMessage()));
        }
    }

    @Transactional
    public CompletableFuture<Result<Void>> insertProduct(ProductInsertDTO productInsertDTO) {
        try {
            Product product = ModelTransformer.insertDtoToProduct(productInsertDTO);

            Result<String> result = handleAndSetRelationship(productInsertDTO, product);
            if (!result.isSuccess()) {
                return CompletableFuture.completedFuture(Result.error(result.getErrorMessage()));
            }

            productRepository.saveAndFlush(product);

            return CompletableFuture.completedFuture(Result.success());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException("Failed to insert product: " + e.getMessage()));
        }
    }

    @Transactional
    public CompletableFuture<Result<Void>> updateProduct(Long productId, ProductInsertDTO productInsertDTO) {
        try {
            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (optionalProduct.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Product with ID: " + productId + "not Found"));
            }

            Long productFoundedId = optionalProduct.get().getId();
            Product product = ModelTransformer.insertDtoToProduct(productInsertDTO);
            product.setUpdatedAt(LocalDateTime.now());
            product.setId(productFoundedId);

            Result<String> result = handleAndSetRelationship(productInsertDTO, product);
            if (!result.isSuccess()) {
                return CompletableFuture.completedFuture(Result.error(result.getErrorMessage()));
            }

            productRepository.saveAndFlush(product);

            return CompletableFuture.completedFuture(Result.success());

        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException("An error occurred while this products was updating", e.getCause()));
        }
    }

    @Transactional
    public CompletableFuture<Result<Void>> deleteProduct(Long productId) {
        try {
            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (optionalProduct.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Product with ID: " + productId + "not found"));
            }

            productRepository.delete(optionalProduct.get());
            return CompletableFuture.completedFuture(Result.success());

        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException("An error occurred while this products was deleting", e.getCause()));

        }

    }

    private Result<String> handleAndSetRelationship(ProductInsertDTO productInsertDTO, Product product) {
        Optional<Category> category = categoryRepository.findById(productInsertDTO.getCategoryId());
        Optional<Brand> brand = brandRepository.findById(productInsertDTO.getBrandId());
        Optional<Subcategory> subcategory = subcategoryRepository.findById(productInsertDTO.getSubcategoryId());
        Optional<Supplier> supplier = supplierRepository.findById(productInsertDTO.getSupplierId());
        Optional<MainCategory> mainCategory = mainCategoryRepository.findById(productInsertDTO.getMainCategoryId());

        Result<String> result = validateRelationships(category, subcategory, brand, mainCategory, supplier);

        if (!result.isSuccess()) {
            return result;
        } else {
            setProductRelationships(product, brand, category, subcategory, supplier, mainCategory);
            return Result.success("Relationship Successfully Established");
        }
    }

    private Result<String> validateRelationships(Optional<Category> category, Optional<Subcategory> subcategory,
                                                 Optional<Brand> brand, Optional<MainCategory> mainCategory, Optional<Supplier> supplier) {
        List<String> errors = new ArrayList<>();

        if (category.isEmpty()) {
            errors.add("Category not found");
        }
        if (subcategory.isEmpty()) {
            errors.add("Subcategory not found");
        }
        if (mainCategory.isEmpty()) {
            errors.add("Main Category not found");
        }
        if (supplier.isEmpty()) {
            errors.add("Supplier not found");
        }

        if (!errors.isEmpty()) {
            String errorMessage = String.join(" --> ", errors); // Join errors with a semicolon and space
            return Result.error(errorMessage);
        } else {
            return Result.success("Successfully Validated");
        }
    }

    private void setProductRelationships(Product product, Optional<Brand> brand, Optional<Category> category,
                                         Optional<Subcategory> subcategory, Optional<Supplier> supplier,
                                         Optional<MainCategory> mainCategory) {
        product.setCategory(category.orElse(null));
        product.setBrand(brand.orElse(null));
        product.setSubcategory(subcategory.orElse(null));
        product.setSupplier(supplier.orElse(null));
        product.setMainCategory(mainCategory.orElse(null));
    }

}



