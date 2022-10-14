package io.github.thebesteric.framework.agile.logger.boot.starter.config;

import io.github.thebesteric.framework.agile.logger.boot.starter.marker.AgileLoggerMarker;
import io.github.thebesteric.framework.agile.logger.commons.AgileLoggerConstant;
import io.github.thebesteric.framework.agile.logger.commons.utils.ClassPathScanner;
import io.github.thebesteric.framework.agile.logger.spring.AgileLoggerFilter;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.enhance.AgileLoggerAnnotatedEnhancer;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.HttpClient;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.MockCache;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.MockProcessor;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.http.DefaultHttpClient;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.processor.TargetMockProcessor;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.processor.TypeMockProcessor;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.processor.ValueMockProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.MappingProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.RecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.mapping.*;
import io.github.thebesteric.framework.agile.logger.spring.processor.record.LogRecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.record.StdoutRecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.scanner.AgileLoggerControllerScanner;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;

/**
 * AgileLoggerAutoConfiguration
 *
 * @author Eric Joe
 * @since 1.0
 */
@Configuration
@EnableAsync
@Import(AgileLoggerInitialization.class)
@ConditionalOnBean(AgileLoggerMarker.class)
@EnableConfigurationProperties(AgileLoggerSpringProperties.class)
public class AgileLoggerAutoConfiguration {

    @Bean
    public AgileLoggerContext agileLoggerContext(ApplicationContext applicationContext) {
        return new AgileLoggerContext(applicationContext);
    }

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "FilterRegister")
    @SuppressWarnings({"unchecked", "rawtypes"})
    public FilterRegistrationBean filterRegister(AgileLoggerContext agileLoggerContext, List<RecordProcessor> recordProcessors) {
        FilterRegistrationBean frBean = new FilterRegistrationBean();
        frBean.setName(AgileLoggerFilter.class.getSimpleName());
        frBean.setFilter(new AgileLoggerFilter(agileLoggerContext, recordProcessors));
        frBean.addUrlPatterns("/*");
        frBean.setOrder(1);
        return frBean;
    }

    @Bean
    public AgileLoggerAnnotatedEnhancer agileLoggerAnnotatedEnhancer(AgileLoggerContext agileLoggerContext) {
        return new AgileLoggerAnnotatedEnhancer(agileLoggerContext);
    }

    @Bean
    public ClassPathScanner agileLoggerControllerScanner(List<MappingProcessor> mappingProcessors) {
        return new AgileLoggerControllerScanner(mappingProcessors);
    }

    // Mapping Processor
    @Configuration
    public static class MappingProcessorConfiguration {
        @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "RequestMappingProcessor")
        public MappingProcessor requestMappingProcessor() {
            return new RequestMappingProcessor();
        }

        @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "DeleteMappingProcessor")
        public MappingProcessor deleteMappingProcessor() {
            return new DeleteMappingProcessor();
        }

        @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "GetMappingProcessor")
        public MappingProcessor getMappingProcessor() {
            return new GetMappingProcessor();
        }

        @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "PatchMappingProcessor")
        public MappingProcessor patchMappingProcessor() {
            return new PatchMappingProcessor();
        }

        @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "PostMappingProcessor")
        public MappingProcessor postMappingProcessor() {
            return new PostMappingProcessor();
        }

        @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "PutMappingProcessor")
        public MappingProcessor putMappingProcessor() {
            return new PutMappingProcessor();
        }
    }

    // Record Processor
    @Configuration
    public static class RecordProcessorConfiguration {
        @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "StdoutRecordProcessor")
        @DependsOn("agileLoggerContext")
        public RecordProcessor stdoutRecordProcessor(AgileLoggerContext agileLoggerContext) {
            return new StdoutRecordProcessor(agileLoggerContext);
        }

        @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "LogRecordProcessor")
        @DependsOn("agileLoggerContext")
        public RecordProcessor logRecordProcessor(AgileLoggerContext agileLoggerContext) {
            return new LogRecordProcessor(agileLoggerContext);
        }
    }

    // Mock Processor
    @Configuration
    @ConditionalOnProperty(prefix = AgileLoggerConstant.PROPERTIES_PREFIX, name = "config.mock.enable", havingValue = "true", matchIfMissing = true)
    public static class MockProcessorConfiguration {
        // Mock processor
        @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "MockCache")
        public MockCache mockCache(AgileLoggerSpringProperties properties) {
            AgileLoggerSpringProperties.Mock mock = properties.getConfig().getMock();
            MockCache.CacheConfiguration configuration = MockCache.CacheConfiguration.builder()
                    .expireAfterWrite(mock.getExpireAfterWrite())
                    .expireAfterAccess(mock.getExpireAfterAccess())
                    .build();
            return new MockCache(configuration);
        }

        @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "ValueMockProcessor")
        public MockProcessor valueMockProcessor(MockCache mockCache) {
            return new ValueMockProcessor(mockCache);
        }

        @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "TargetMockProcessor")
        public MockProcessor targetMockProcessor(MockCache mockCache, @Nullable HttpClient httpClient) {
            if (httpClient == null) {
                httpClient = new DefaultHttpClient();
            }
            return new TargetMockProcessor(mockCache, httpClient);
        }

        @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "TypeMockProcessor")
        public MockProcessor typeMockProcessor(MockCache mockCache) {
            return new TypeMockProcessor(mockCache);
        }
    }

}
