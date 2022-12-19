package io.github.thebesteric.framework.agile.logger.rpc.feign.config;

import io.github.thebesteric.framework.agile.logger.commons.AgileLoggerConstant;
import io.github.thebesteric.framework.agile.logger.rpc.feign.processor.FeignLHandler;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * AgileLoggerFeignAutoConfiguration
 *
 * @author Eric Joe
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(AgileLoggerSpringProperties.class)
@ConditionalOnProperty(prefix = AgileLoggerConstant.PROPERTIES_PREFIX, name = "rpc.feign.enable", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(feign.Logger.class)
@ConditionalOnBean(AgileLoggerContext.class)
public class AgileLoggerFeignAutoConfiguration {

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "FeignLogLevel")
    public feign.Logger.Level feignLogLevel() {
        return feign.Logger.Level.FULL;
    }

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "FeignLogger")
    @DependsOn(AgileLoggerConstant.BeanName.AGILE_LOGGER_CONTEXT)
    public feign.Logger feignLogger(ObjectProvider<AgileLoggerContext> agileLoggerContext) {
        assert (agileLoggerContext.getIfUnique() != null) : AgileLoggerConstant.ASSERT_AGILE_LOGGER_CONTEXT_NOT_NULL;
        return new FeignLHandler(agileLoggerContext.getIfUnique());
    }
}
