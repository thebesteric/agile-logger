package io.github.thebesteric.framework.agile.logger.plugin.mybatis.config;

import io.github.thebesteric.framework.agile.logger.commons.AgileLoggerConstant;
import io.github.thebesteric.framework.agile.logger.core.domain.SqlCommandType;
import io.github.thebesteric.framework.agile.logger.plugin.mybatis.MyBatisPrintSQLInterceptor;
import io.github.thebesteric.framework.agile.logger.plugin.mybatis.annotation.ConditionalOnSqlCommandType;
import io.github.thebesteric.framework.agile.logger.plugin.mybatis.processor.StatementProcessor;
import io.github.thebesteric.framework.agile.logger.plugin.mybatis.processor.impl.DeleteStatementProcessor;
import io.github.thebesteric.framework.agile.logger.plugin.mybatis.processor.impl.InsertStatementProcessor;
import io.github.thebesteric.framework.agile.logger.plugin.mybatis.processor.impl.SelectStatementProcessor;
import io.github.thebesteric.framework.agile.logger.plugin.mybatis.processor.impl.UpdateStatementProcessor;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(AgileLoggerSpringProperties.class)
@ConditionalOnClass(SqlSessionFactory.class)
@ConditionalOnProperty(prefix = AgileLoggerConstant.PROPERTIES_PREFIX, name = "plugins.my-batis.enable", havingValue = "true", matchIfMissing = true)
public class AgileLoggerMybatisAutoConfiguration {

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "MyBatisPrintSQLInterceptor")
    public MyBatisPrintSQLInterceptor interceptor(ObjectProvider<AgileLoggerContext> agileLoggerContext, List<StatementProcessor> statementProcessors) {
        assert (agileLoggerContext.getIfUnique() != null) : AgileLoggerConstant.ASSERT_AGILE_LOGGER_CONTEXT_NOT_NULL;
        return new MyBatisPrintSQLInterceptor(agileLoggerContext.getIfUnique(), statementProcessors);
    }

    @ConditionalOnSqlCommandType(SqlCommandType.UPDATE)
    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "UpdateStatementProcessor")
    public StatementProcessor updateStatementProcessor() {
        return new UpdateStatementProcessor();
    }

    @ConditionalOnSqlCommandType(SqlCommandType.DELETE)
    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "DeleteStatementProcessor")
    public StatementProcessor deleteStatementProcessor() {
        return new DeleteStatementProcessor();
    }

    @ConditionalOnSqlCommandType(SqlCommandType.INSERT)
    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "InsertStatementProcessor")
    public StatementProcessor insertStatementProcessor() {
        return new InsertStatementProcessor();
    }

    @ConditionalOnSqlCommandType(SqlCommandType.SELECT)
    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "SelectStatementProcessor")
    public StatementProcessor selectStatementProcessor() {
        return new SelectStatementProcessor();
    }

}
