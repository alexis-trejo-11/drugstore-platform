package microservice.product_service.Config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "categoryWithProducts",
                "categoryWithSubcategories",
                "categoryWithSubcategoriesAndProducts",
                "mainCategoryWithCategoriesAndSubcategories",
                "mainCategoryWithProducts",
                "productsBySupplier",
                "productsByCategory",
                "productsBySubcategory",
                "productsById",
                "productsByCategory",
                "productById",
                "allProductsSortedByCategoryHierarchy",
                "allSubcategories",
                "subcategoryWithProducts",
                "supplierById",
                "supplierByName",
                "allSuppliersSortedByName",
                "validateExisitingProduct",
                "validateExistingSupplier"
        );        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(1, TimeUnit.HOURS));
        cacheManager.setAllowNullValues(false);
        cacheManager.setAsyncCacheMode(true);


        return cacheManager;
    }
}