package io.github.thebesteric.framework.agile.logger.plugin.cache.spring.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.thebesteric.framework.agile.logger.commons.AgileLoggerConstant;
import io.github.thebesteric.framework.agile.logger.plugin.cache.spring.record.CacheRecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.processor.RecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import java.util.concurrent.TimeUnit;

/**
 * AgileLoggerCacheAutoConfiguration
 *
 * @author Eric Joe
 * @version 1.0
 */
@Configuration
@Import(AgileLoggerCacheInitialization.class)
@EnableConfigurationProperties(AgileLoggerSpringProperties.class)
@ConditionalOnProperty(prefix = AgileLoggerConstant.PROPERTIES_PREFIX, name = "log-mode", havingValue = "cache")
public class AgileLoggerCacheAutoConfiguration {

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "Cache")
    public Cache<String, Object> cache(AgileLoggerSpringProperties properties) {
        AgileLoggerSpringProperties.Plugins plugins = properties.getPlugins();
        return Caffeine.newBuilder()
                .initialCapacity(plugins.getCache().getInitialCapacity())
                .maximumSize(plugins.getCache().getMaximumSize())
                .expireAfterWrite(plugins.getCache().getExpiredTime(), TimeUnit.MILLISECONDS)
                .build();
    }

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "DatabaseRecordProcessor")
    @DependsOn(AgileLoggerConstant.BeanName.AGILE_LOGGER_CONTEXT)
    public RecordProcessor cacheRecordProcessor(ObjectProvider<AgileLoggerContext> agileLoggerContext,
                                                @Qualifier(AgileLoggerConstant.BEAN_NAME_PREFIX + "Cache")
                                                Cache<String, Object> cache) {
        assert (agileLoggerContext.getIfUnique() != null) : AgileLoggerConstant.ASSERT_AGILE_LOGGER_CONTEXT_NOT_NULL;
        return new CacheRecordProcessor(agileLoggerContext.getIfUnique(), cache);
    }
}
