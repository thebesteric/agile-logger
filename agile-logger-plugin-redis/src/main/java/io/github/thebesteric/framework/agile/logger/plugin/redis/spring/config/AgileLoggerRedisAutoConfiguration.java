package io.github.thebesteric.framework.agile.logger.plugin.redis.spring.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.thebesteric.framework.agile.logger.commons.AgileLoggerConstant;
import io.github.thebesteric.framework.agile.logger.plugin.redis.spring.record.RedisRecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.processor.RecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * AgileLoggerRedisAutoConfiguration
 *
 * @author Eric Joe
 * @version 1.0
 */
@Configuration
@Import(AgileLoggerRedisInitialization.class)
@EnableConfigurationProperties(AgileLoggerSpringProperties.class)
public class AgileLoggerRedisAutoConfiguration {

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "GenericObjectPoolConfig")
    @SuppressWarnings("rawtypes")
    public GenericObjectPoolConfig genericObjectPoolConfig(AgileLoggerSpringProperties properties) {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig<>();
        genericObjectPoolConfig.setMaxIdle(properties.getRedis().getMaxIdle());
        genericObjectPoolConfig.setMinIdle(properties.getRedis().getMinIdle());
        genericObjectPoolConfig.setMaxTotal(properties.getRedis().getMaxActive());
        genericObjectPoolConfig.setMaxWait(Duration.ofMillis(properties.getRedis().getMaxWait()));
        return genericObjectPoolConfig;
    }

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "RedisConfiguration")
    public RedisConfiguration redisConfiguration(AgileLoggerSpringProperties properties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(properties.getRedis().getDatabase());
        redisStandaloneConfiguration.setHostName(properties.getRedis().getHost());
        redisStandaloneConfiguration.setPort(properties.getRedis().getPort());
        redisStandaloneConfiguration.setUsername(properties.getRedis().getUsername());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(properties.getRedis().getPassword()));
        return redisStandaloneConfiguration;
    }

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "LettuceConnectionFactory")
    @SuppressWarnings("rawtypes")
    public LettuceConnectionFactory lettuceConnectionFactory(@Qualifier(AgileLoggerConstant.BEAN_NAME_PREFIX + "GenericObjectPoolConfig")
                                                             GenericObjectPoolConfig genericObjectPoolConfig,
                                                             @Qualifier(AgileLoggerConstant.BEAN_NAME_PREFIX + "RedisConfiguration")
                                                             RedisConfiguration redisConfiguration,
                                                             AgileLoggerSpringProperties properties) {
        return new LettuceConnectionFactory(redisConfiguration, LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(properties.getRedis().getTimeout()))
                .shutdownTimeout(Duration.ofMillis(properties.getRedis().getShutdownTimeout()))
                .poolConfig(genericObjectPoolConfig)
                .build());
    }

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "RedisTemplate")
    @ConditionalOnProperty(prefix = AgileLoggerConstant.PROPERTIES_PREFIX, name = "log-mode", havingValue = "redis")
    @DependsOn(AgileLoggerConstant.BEAN_NAME_PREFIX + "LettuceConnectionFactory")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier(AgileLoggerConstant.BEAN_NAME_PREFIX + "LettuceConnectionFactory")
                                                       LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);

        // Key Serializer
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);

        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        jacksonSerializer.setObjectMapper(objectMapper);

        // Value Serializer
        template.setValueSerializer(jacksonSerializer);
        template.setHashValueSerializer(jacksonSerializer);
        template.setDefaultSerializer(jacksonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "RedisTRecordProcessor")
    @ConditionalOnProperty(prefix = AgileLoggerConstant.PROPERTIES_PREFIX, name = "log-mode", havingValue = "redis")
    @DependsOn("agileLoggerContext")
    public RecordProcessor redisRecordProcessor(@Qualifier(AgileLoggerConstant.BEAN_NAME_PREFIX + "RedisTemplate")
                                                RedisTemplate<String, Object> redisTemplate,
                                                @Nullable AgileLoggerContext agileLoggerContext) {
        return new RedisRecordProcessor(redisTemplate, agileLoggerContext);
    }

}
