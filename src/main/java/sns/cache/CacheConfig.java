package sns.cache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.expiry.Expiry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sns.cache.entity.DistanceFactorCalculationRequest;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager()
    {
        CacheManager cacheManager  = CacheManagerBuilder.newCacheManagerBuilder().build();
        cacheManager.init();
        return cacheManager;
    }

    @Bean
    public Cache<String, DistanceFactorCalculationRequest> distanceFactorCalculationRequestsCache(CacheManager cacheManager) {

        CacheConfigurationBuilder builder = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, DistanceFactorCalculationRequest.class,
                ResourcePoolsBuilder.heap(10)).withExpiry(Expirations.timeToLiveExpiration(Duration.of(20, TimeUnit.MINUTES)));
        return cacheManager.createCache("distanceFactorCalculationRequests", builder);
    }
}