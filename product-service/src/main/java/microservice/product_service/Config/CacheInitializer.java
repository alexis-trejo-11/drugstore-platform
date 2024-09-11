package microservice.product_service.Config;

import microservice.product_service.Model.*;
import microservice.product_service.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Configuration
public class CacheInitializer {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ProductRepository productRepository;

    @PostConstruct
    public void initializeCaches() {
        Cache categoryWithProductsCache = cacheManager.getCache("categoryWithProducts");
        Cache categoryWithSubcategoriesCache = cacheManager.getCache("categoryWithSubcategories");
        Cache categoryWithSubcategoriesAndProductsCache = cacheManager.getCache("categoryWithSubcategoriesAndProducts");
        Cache mainCategoryWithCategoriesAndSubcategoriesCache = cacheManager.getCache("mainCategoryWithCategoriesAndSubcategories");
        Cache mainCategoryWithProductsCache = cacheManager.getCache("mainCategoryWithProducts");
        Cache productsBySupplierCache = cacheManager.getCache("productsBySupplier");
        Cache productsByCategoryCache = cacheManager.getCache("productsByCategory");
        Cache productsBySubcategoryCache = cacheManager.getCache("productsBySubcategory");
        Cache productsByIdCache = cacheManager.getCache("productsById");
        Cache productByIdCache = cacheManager.getCache("productById");
        Cache allProductsSortedByCategoryHierarchyCache = cacheManager.getCache("allProductsSortedByCategoryHierarchy");
        Cache allSubcategoriesCache = cacheManager.getCache("allSubcategories");
        Cache subcategoryWithProductsCache = cacheManager.getCache("subcategoryWithProducts");
        Cache supplierByIdCache = cacheManager.getCache("supplierById");
        Cache supplierByNameCache = cacheManager.getCache("supplierByName");
        Cache allSuppliersSortedByNameCache = cacheManager.getCache("allSuppliersSortedByName");
        Cache validateExisitingProductCache = cacheManager.getCache("validateExisitingProduct");

        if (categoryWithProductsCache != null) {
            List<Category> categories = productRepository.findAllCategories();
            categories.forEach(category -> categoryWithProductsCache.put(category.getId(), fetchCategoryWithProducts(category)));
        }

        if (categoryWithSubcategoriesCache != null) {
            List<Category> categories = productRepository.findAllCategories();
            categories.forEach(category -> categoryWithSubcategoriesCache.put(category.getId(), fetchCategoryWithSubcategories(category)));
        }

        if (categoryWithSubcategoriesAndProductsCache != null) {
            List<Category> categories = productRepository.findAllCategories();
            categories.forEach(category -> categoryWithSubcategoriesAndProductsCache.put(category.getId(), fetchCategoryWithSubcategoriesAndProducts(category)));
        }

        if (mainCategoryWithCategoriesAndSubcategoriesCache != null) {
            List<MainCategory> mainCategories = productRepository.findAllMainCategories();
            mainCategories.forEach(mainCategory -> mainCategoryWithCategoriesAndSubcategoriesCache.put(mainCategory.getId(), fetchMainCategoryWithCategoriesAndSubcategories(mainCategory)));
        }

        if (mainCategoryWithProductsCache != null) {
            List<MainCategory> mainCategories = productRepository.findAllMainCategories();
            mainCategories.forEach(mainCategory -> mainCategoryWithProductsCache.put(mainCategory.getId(), fetchMainCategoryWithProducts(mainCategory)));
        }

        if (productsBySupplierCache != null) {
            List<Supplier> suppliers = productRepository.findAllSuppliers();
            suppliers.forEach(supplier -> productsBySupplierCache.put(supplier.getId(), fetchProductsBySupplier(supplier)));
        }

        if (productsByCategoryCache != null) {
            List<Category> categories = productRepository.findAllCategories();
            categories.forEach(category -> productsByCategoryCache.put(category.getId(), fetchProductsByCategory(category)));
        }

        if (productsBySubcategoryCache != null) {
            List<Subcategory> subcategories = productRepository.findAllSubcategories();
            subcategories.forEach(subcategory -> productsBySubcategoryCache.put(subcategory.getId(), fetchProductsBySubcategory(subcategory)));
        }

        if (productsByIdCache != null) {
            List<Product> products = productRepository.findAll(); // Assuming all products are fetched
            products.forEach(product -> productsByIdCache.put(product.getId(), product));
        }

        if (productByIdCache != null) {
            List<Product> products = productRepository.findAll(); // Assuming all products are fetched
            products.forEach(product -> productByIdCache.put(product.getId(), product));
        }

        if (allProductsSortedByCategoryHierarchyCache != null) {
            List<Product> products = productRepository.findAllSortedByCategoryHierarchy(Pageable.unpaged()).getContent();
            allProductsSortedByCategoryHierarchyCache.put("allProducts", products);
        }

        if (allSubcategoriesCache != null) {
            List<Subcategory> subcategories = productRepository.findAllSubcategories();
            allSubcategoriesCache.put("allSubcategories", subcategories);
        }

        if (subcategoryWithProductsCache != null) {
            List<Subcategory> subcategories = productRepository.findAllSubcategories();
            subcategories.forEach(subcategory -> subcategoryWithProductsCache.put(subcategory.getId(), fetchSubcategoryWithProducts(subcategory)));
        }

        if (supplierByIdCache != null) {
            List<Supplier> suppliers = productRepository.findAllSuppliers();
            suppliers.forEach(supplier -> supplierByIdCache.put(supplier.getId(), supplier));
        }

        if (supplierByNameCache != null) {
            List<Supplier> suppliers = productRepository.findAllSuppliers();
            suppliers.forEach(supplier -> supplierByNameCache.put(supplier.getName(), supplier));
        }

        if (allSuppliersSortedByNameCache != null) {
            List<Supplier> suppliers = productRepository.findAllSuppliers();
            allSuppliersSortedByNameCache.put("allSuppliers", suppliers);
        }

        if (validateExisitingProductCache != null) {
            List<Product> products = productRepository.findAll();
            products.forEach(product -> validateExisitingProductCache.put(product.getId(), Boolean.TRUE));
        }
    }

    private Object fetchCategoryWithProducts(Category category) {
        return productRepository.findByCategory_Id(category.getId(), Pageable.unpaged()).getContent();
    }

    private Object fetchCategoryWithSubcategories(Category category) {
        // Implement method to fetch categories with subcategories
        return new Object();
    }

    private Object fetchCategoryWithSubcategoriesAndProducts(Category category) {
        // Implement method to fetch categories with subcategories and products
        return new Object();
    }

    private Object fetchMainCategoryWithCategoriesAndSubcategories(MainCategory mainCategory) {
        // Implement method to fetch main category with categories and subcategories
        return new Object();
    }

    private Object fetchMainCategoryWithProducts(MainCategory mainCategory) {
        // Implement method to fetch main category with products
        return new Object();
    }

    private Object fetchProductsBySupplier(Supplier supplier) {
        return productRepository.findBySupplier_Id(supplier.getId(), Pageable.unpaged()).getContent();
    }

    private Object fetchProductsByCategory(Category category) {
        return productRepository.findByCategory_Id(category.getId(), Pageable.unpaged()).getContent();
    }

    private Object fetchProductsBySubcategory(Subcategory subcategory) {
        return productRepository.findBySubcategory_Id(subcategory.getId(), Pageable.unpaged()).getContent();
    }

    private Object fetchSubcategoryWithProducts(Subcategory subcategory) {
        return productRepository.findBySubcategory_Id(subcategory.getId(), Pageable.unpaged()).getContent();
    }
}
