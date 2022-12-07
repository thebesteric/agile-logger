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
import org.springframework.beans.factory.ObjectProvider;
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
@ConditionalOnProperty(prefix = AgileLoggerConstant.PROPERTIES_PREFIX, name = "log-mode", havingValue = "redis")
public class AgileLoggerRedisAutoConfiguration {

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "GenericObjectPoolConfig")
    @SuppressWarnings("rawtypes")
    public GenericObjectPoolConfig genericObjectPoolConfig(AgileLoggerSpringProperties properties) {
        AgileLoggerSpringProperties.Plugins plugins = properties.getPlugins();
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig<>();
        genericObjectPoolConfig.setMaxIdle(plugins.getRedis().getMaxIdle());
        genericObjectPoolConfig.setMinIdle(plugins.getRedis().getMinIdle());
        genericObjectPoolConfig.setMaxTotal(plugins.getRedis().getMaxActive());
        genericObjectPoolConfig.setMaxWait(Duration.ofMillis(plugins.getRedis().getMaxWait()));
        return genericObjectPoolConfig;
    }

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "RedisConfiguration")
    public RedisConfiguration redisConfiguration(AgileLoggerSpringProperties properties) {
        AgileLoggerSpringProperties.Plugins plugins = properties.getPlugins();
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(plugins.getRedis().getDatabase());
        redisStandaloneConfiguration.setHostName(plugins.getRedis().getHost());
        redisStandaloneConfiguration.setPort(plugins.getRedis().getPort());
        redisStandaloneConfiguration.setUsername(plugins.getRedis().getUsername());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(plugins.getRedis().getPassword()));
        return redisStandaloneConfiguration;
    }

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "LettuceConnectionFactory")
    @SuppressWarnings("rawtypes")
    public LettuceConnectionFactory lettuceConnectionFactory(@Qualifier(AgileLoggerConstant.BEAN_NAME_PREFIX + "GenericObjectPoolConfig")
                                                             GenericObjectPoolConfig genericObjectPoolConfig,
                                                             @Qualifier(AgileLoggerConstant.BEAN_NAME_PREFIX + "RedisConfiguration")
                                                             RedisConfiguration redisConfiguration,
                                                             AgileLoggerSpringProperties properties) {
        AgileLoggerSpringProperties.Plugins plugins = properties.getPlugins();
        return new LettuceConnectionFactory(redisConfiguration, LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(plugins.getRedis().getTimeout()))
                .shutdownTimeout(Duration.ofMillis(plugins.getRedis().getShutdownTimeout()))
                .poolConfig(genericObjectPoolConfig)
                .build());
    }

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "RedisTemplate")
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

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "RedisRecordProcessor")
    @DependsOn(AgileLoggerConstant.BeanName.AGILE_LOGGER_CONTEXT)
    public RecordProcessor redisRecordProcessor(ObjectProvider<AgileLoggerContext> agileLoggerContext,
                                                @Qualifier(AgileLoggerConstant.BEAN_NAME_PREFIX + "RedisTemplate")
                                                RedisTemplate<String, Object> redisTemplate) {
        assert (agileLoggerContext.getIfUnique() != null) : AgileLoggerConstant.ASSERT_AGILE_LOGGER_CONTEXT_NOT_NULL;
        return new RedisRecordProcessor(agileLoggerContext.getIfUnique(), redisTemplate);
    }

}
