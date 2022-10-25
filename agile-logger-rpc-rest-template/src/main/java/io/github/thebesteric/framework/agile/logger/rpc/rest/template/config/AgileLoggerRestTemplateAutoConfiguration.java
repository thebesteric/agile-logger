package io.github.thebesteric.framework.agile.logger.rpc.rest.template.config;

import io.github.thebesteric.framework.agile.logger.commons.AgileLoggerConstant;
import io.github.thebesteric.framework.agile.logger.commons.utils.CollectionUtils;
import io.github.thebesteric.framework.agile.logger.rpc.rest.template.processor.RestTemplateHandler;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * AgileLoggerFeignAutoConfiguration
 *
 * @author Eric Joe
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(AgileLoggerSpringProperties.class)
@ConditionalOnProperty(prefix = AgileLoggerConstant.PROPERTIES_PREFIX, name = "rpc.rest-template.enable", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(RestTemplate.class)
public class AgileLoggerRestTemplateAutoConfiguration {

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "restTemplateHandler")
    @DependsOn("agileLoggerContext")
    public ClientHttpRequestInterceptor restTemplateHandler(@Nullable AgileLoggerContext agileLoggerContext) {
        assert (agileLoggerContext != null) : AgileLoggerConstant.ASSERT_AGILE_LOGGER_CONTEXT_NOT_NULL;
        return new RestTemplateHandler(agileLoggerContext);
    }

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "RestTemplateInitializer")
    public SmartInitializingSingleton restTemplateInitializer(List<RestTemplate> restTemplates,
                                                              @Qualifier(AgileLoggerConstant.BEAN_NAME_PREFIX + "restTemplateHandler")
                                                              ClientHttpRequestInterceptor restTemplateHandler) {
        return () -> {
            for (RestTemplate restTemplate : restTemplates) {
                List<ClientHttpRequestInterceptor> interceptors = CollectionUtils.getList(restTemplate.getInterceptors());
                interceptors.add(restTemplateHandler);
                restTemplate.setInterceptors(interceptors);
            }
        };
    }

}
