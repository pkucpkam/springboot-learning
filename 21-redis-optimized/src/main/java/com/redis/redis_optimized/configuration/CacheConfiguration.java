package com.redis.redis_optimized.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableCaching
@Slf4j
public class CacheConfiguration {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.cache.redis.default-ttl:10m}")
    private Duration defaultTtl;

    @Value("${spring.data.cache.redis.cache-ttls.customers:5m}")
    private Duration customersTtl;

    @Value("${spring.data.redis.database:0}")
    private int redisDatabase;

    @Value("${spring.data.redis.cluster.nodes:}")
    private String clusterNodes;

    @Value("${spring.data.redis.ssl:false}")
    private boolean useSsl;

    @Value("${spring.data.redis.lettuce.pool.max-total:100}")
    private int maxTotal;

    @Value("${spring.data.redis.lettuce.pool.max-idle:50}")
    private int maxIdle;

    @Value("${spring.data.redis.lettuce.pool.min-idle:10}")
    private int minIdle;

    @Value("${spring.data.redis.lettuce.pool.max-wait:1s}")
    private Duration maxWait;

    @Value("${spring.data.redis.timeout:2s}")
    private Duration redisTimeout;


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        log.info("Initializing RedisConnectionFactory with host: {}, port: {}, cluster: {}", redisHost, redisPort, clusterNodes.isEmpty() ? "standalone" : "cluster");

        LettuceClientConfiguration clientConfig = buildLettuceClientConfiguration();

        if (!clusterNodes.isEmpty()) {
            // Cluster mode
            RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration();

            String[] nodes = clusterNodes.split(",");
            if (nodes.length == 0 || nodes[0].trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid Redis cluster nodes configuration");
            }

            for (String node : nodes) {
                String[] parts = node.split(":");

                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid Redis node format: " + node);
                }
                try {
                    clusterConfig.clusterNode(parts[0], Integer.parseInt(parts[1]));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid port in Redis node: " + node, e);
                }
            }
            if (!redisPassword.isEmpty()) {
                clusterConfig.setPassword(redisPassword);
            }
            return new LettuceConnectionFactory(clusterConfig, clientConfig);
        } else {
            // Standalone mode
            RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
            standaloneConfig.setDatabase(redisDatabase);
            if (!redisPassword.isEmpty()) {
                standaloneConfig.setPassword(redisPassword);
            }

            return new LettuceConnectionFactory(standaloneConfig, clientConfig);
        }
    }

    private GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig() {
        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWait(maxWait);
        poolConfig.setTestOnBorrow(true);
        return poolConfig;
    }

    private LettuceClientConfiguration buildLettuceClientConfiguration() {
        // Pool config with specific type to avoid mismatch
        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = poolConfig();

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder =
                LettucePoolingClientConfiguration.builder()
                        .poolConfig(poolConfig)
                        .commandTimeout(redisTimeout);

        // SSL if enabled - useSsl() does not take arguments, it's a toggle
        if (useSsl) {
            builder.useSsl();
        }

        // Cluster topology refresh if cluster
        if (!clusterNodes.isEmpty()) {
            ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                    .enablePeriodicRefresh(Duration.ofMinutes(10))
                    .enableAllAdaptiveRefreshTriggers()
                    .build();
            ClientOptions clientOptions = ClusterClientOptions.builder()
                    .topologyRefreshOptions(topologyRefreshOptions)
                    .build();
            builder.clientOptions(clientOptions);
        }

        return builder.build();
    }

    @Bean
    public RedisCacheConfiguration defaultRedisCacheConfig(ObjectMapper objectMapper) {
        // Key serializer
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        RedisSerializer<Object> valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(defaultTtl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .disableCachingNullValues();
    }


    private void registerCaches(Map<String, RedisCacheConfiguration> configs,
                                      RedisCacheConfiguration defaultConfig) {
        configs.put("customers", defaultConfig.entryTtl(customersTtl));
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, GenericJackson2JsonRedisSerializer redisSerializer) {
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();


        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer));

        registerCaches(configs, defaultConfig);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configs)
                .transactionAware()
                .build();
    }

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new CacheErrorHandler() {
            private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CacheConfiguration.class);

            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis GET error - cache: {}, key: {}, err: {}", cache != null ? cache.getName() : "null", key, exception.getMessage());

            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn("Redis PUT error - cache: {}, key: {}, err: {}", cache != null ? cache.getName() : "null", key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis EVICT error - cache: {}, key: {}, err: {}", cache != null ? cache.getName() : "null", key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("Redis CLEAR error - cache: {}, err: {}", cache != null ? cache.getName() : "null", exception.getMessage());
            }
        };
    }
}
