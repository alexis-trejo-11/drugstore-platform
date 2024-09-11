package microservice.product_service.Service.DomainServices;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductRelationsIDs;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.product_service.Model.*;
import microservice.product_service.Repository.*;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ProductDomainService {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final SupplierRepository supplierRepository;

    public ProductDomainService(BrandRepository brandRepository, CategoryRepository categoryRepository, MainCategoryRepository mainCategoryRepository, SubcategoryRepository subcategoryRepository, SupplierRepository supplierRepository) {
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.mainCategoryRepository = mainCategoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public Result<Void> handleRelationShips(ProductRelationsIDs productRelationsIDs, Product product) {
        CompletableFuture<MainCategory> mainCategoryFuture = CompletableFuture.supplyAsync(() -> mainCategoryRepository.findById(productRelationsIDs.getMainCategoryId()).orElse(null));
        CompletableFuture<Category> categoryFuture = CompletableFuture.supplyAsync(() -> categoryRepository.findById(productRelationsIDs.getCategoryId()).orElse(null));
        CompletableFuture<Subcategory> subcategoryFuture = CompletableFuture.supplyAsync(() -> subcategoryRepository.findById(productRelationsIDs.getSubcategoryId()).orElse(null));
        CompletableFuture<Brand> brandFuture = CompletableFuture.supplyAsync(() -> brandRepository.findById(productRelationsIDs.getBrandId()).orElse(null));
        CompletableFuture<Supplier> supplierFuture = CompletableFuture.supplyAsync(() -> supplierRepository.findById(productRelationsIDs.getSupplierId()).orElse(null));

            CompletableFuture<Void> allOf = CompletableFuture.allOf(mainCategoryFuture, categoryFuture, subcategoryFuture, brandFuture, supplierFuture);
            allOf.join(); // Wait for all futures to complete

            MainCategory mainCategory = mainCategoryFuture.join();
            if (mainCategory == null) {
                return Result.error("Invalid Main Category");
            }
            Hibernate.initialize(mainCategory);

            Category category = categoryFuture.join();
            if (category == null) {
                return Result.error("Invalid Category");
            }
            Hibernate.initialize(category);

            Subcategory subcategory = subcategoryFuture.join();
            if (subcategory == null) {
                return Result.error("Invalid SubCategory");
            }
            Hibernate.initialize(subcategory);

            Brand brand = brandFuture.join();
            if (brand == null) {
                return Result.error("Invalid Brand");
            }
            Hibernate.initialize(brand);

            Supplier supplier = supplierFuture.join();
            if (supplier == null) {
                return Result.error("Invalid Supplier");
            }
            Hibernate.initialize(supplier);
            addRelationship(product, mainCategory, category, subcategory, supplier, brand);
            return Result.success();
    }

    private void addRelationship(Product product, MainCategory mainCategory, Category category, Subcategory subcategory, Supplier supplier, Brand brand) {
        product.setMainCategory(mainCategory);
        product.setCategory(category);
        product.setSubcategory(subcategory);
        product.setSupplier(supplier);
        product.setBrand(brand);
    }
}