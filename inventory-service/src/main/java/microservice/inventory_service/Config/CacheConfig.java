package microservice.inventory_service.Config;

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
                "transactionSummary",
                "inventoryItemsByProduct",
                "inventoryItems",
                "inventoryItemsByProduct",
                "inventorySummary",
                "transactionsBySupplier",
                "transactionsByStatus",
                "nearToExpireTransactions",
                "transactionById",
                "validateInventoryItems",
                "validateInventoryTransactions",
                "validateProduct",
                "validateSupplier"
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(60, TimeUnit.MINUTES));
        cacheManager.setAllowNullValues(false);
        cacheManager.setAsyncCacheMode(true);
        return cacheManager;
    }
}