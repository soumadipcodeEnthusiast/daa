package com.diff.studio.algolens;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine cache configuration for AlgoLens.
 *
 * Cache: "llmResults"
 *   - Key: SHA-256 of the prompt (snippet + evidence)
 *   - Max size: 500 entries
 *   - Expires after write: 24 hours
 */
@Configuration
@EnableCaching
public class AlgoLensCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("llmResults");
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(24, TimeUnit.HOURS));
        return manager;
    }
}
