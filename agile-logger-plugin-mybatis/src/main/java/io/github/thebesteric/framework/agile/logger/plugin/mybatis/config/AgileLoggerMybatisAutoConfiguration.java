package io.github.thebesteric.framework.agile.logger.plugin.mybatis.config;

import io.github.thebesteric.framework.agile.logger.commons.AgileLoggerConstant;
import io.github.thebesteric.framework.agile.logger.plugin.mybatis.MyBatisPrintSQLInterceptor;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AgileLoggerSpringProperties.class)
@ConditionalOnClass(SqlSessionFactory.class)
@ConditionalOnProperty(prefix = AgileLoggerConstant.PROPERTIES_PREFIX, name = "plugins.my-batis.enable", havingValue = "true")
public class AgileLoggerMybatisAutoConfiguration {

    @Bean
    public MyBatisPrintSQLInterceptor interceptor(ObjectProvider<AgileLoggerContext> agileLoggerContext) {
        assert (agileLoggerContext.getIfUnique() != null) : AgileLoggerConstant.ASSERT_AGILE_LOGGER_CONTEXT_NOT_NULL;
        return new MyBatisPrintSQLInterceptor(agileLoggerContext.getIfUnique());
    }

}
