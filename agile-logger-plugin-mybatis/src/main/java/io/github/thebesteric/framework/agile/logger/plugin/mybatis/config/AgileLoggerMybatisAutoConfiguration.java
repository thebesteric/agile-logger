package io.github.thebesteric.framework.agile.logger.plugin.mybatis.config;

import io.github.thebesteric.framework.agile.logger.plugin.mybatis.MyBatisPrintSQLInterceptor;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(SqlSessionFactory.class)
@EnableConfigurationProperties(AgileLoggerSpringProperties.class)
public class AgileLoggerMybatisAutoConfiguration {

    @Bean
    public MyBatisPrintSQLInterceptor interceptor() {
        return new MyBatisPrintSQLInterceptor();
    }

}
