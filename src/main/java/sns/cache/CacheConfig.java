package sns.cache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.expiry.Expiry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sns.cache.entity.DistanceFactorCalculationRequest;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Value("${sns.cache.maxSize}")
    private Integer maxSize;
    @Value("${sns.cache.maxLifeTime}")
    private Integer maxLifeTime;
    @Bean
    public CacheManager cacheManager()
    {
       // CacheManager cacheManager  = CacheManagerBuilder.newCacheManagerBuilder().build();
        PersistentCacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence("."))
                .build();


        cacheManager.init();
        return cacheManager;
    }

    @Bean
    public Cache<String, DistanceFactorCalculationRequest> distanceFactorCalculationRequestsCache(CacheManager cacheManager) {

        CacheConfigurationBuilder builder = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                        String.class,
                        DistanceFactorCalculationRequest.class,
                        ResourcePoolsBuilder
                                .newResourcePoolsBuilder()
                                .disk(100, MemoryUnit.MB)
                                .heap(maxSize, EntryUnit.ENTRIES))
                .withExpiry(
                        Expirations.timeToLiveExpiration(
                                Duration.of(maxLifeTime, TimeUnit.HOURS)));
        return cacheManager.createCache("distanceFactorCalculationRequests", builder);
    }
}