package io.github.thebesteric.framework.agile.logger.plugin.database.spring.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.thebesteric.framework.agile.logger.commons.AgileLoggerConstant;
import io.github.thebesteric.framework.agile.logger.plugin.database.spring.jdbc.AgileLoggerJdbcTemplate;
import io.github.thebesteric.framework.agile.logger.plugin.database.spring.record.DatabaseRecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.processor.RecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;

/**
 * AgileLoggerDatabaseAutoConfiguration
 *
 * @author Eric Joe
 * @version 1.0
 */
@Configuration
@Import(AgileLoggerDatabaseInitialization.class)
@EnableConfigurationProperties(AgileLoggerSpringProperties.class)
@ConditionalOnProperty(prefix = AgileLoggerConstant.PROPERTIES_PREFIX, name = "log-mode", havingValue = "database")
public class AgileLoggerDatabaseAutoConfiguration {

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "HikariDataSource")
    public DataSource hikariDataSource(AgileLoggerSpringProperties properties) {
        AgileLoggerSpringProperties.Plugins plugins = properties.getPlugins();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(plugins.getDatabase().getUrl());
        config.setUsername(plugins.getDatabase().getUsername());
        config.setPassword(plugins.getDatabase().getPassword());
        config.setDriverClassName(plugins.getDatabase().getDriverClassName());
        config.setMinimumIdle(plugins.getDatabase().getMinIdle());
        config.setMaximumPoolSize(plugins.getDatabase().getMaxActive());
        return new HikariDataSource(config);
    }

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "JdbcTemplate")
    public AgileLoggerJdbcTemplate jdbcTemplate(@Qualifier(AgileLoggerConstant.BEAN_NAME_PREFIX + "HikariDataSource")
                                                DataSource hikariDataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(hikariDataSource);
        return new AgileLoggerJdbcTemplate(jdbcTemplate);
    }

    @Bean(name = AgileLoggerConstant.BEAN_NAME_PREFIX + "DatabaseRecordProcessor")
    @DependsOn("agileLoggerContext")
    public RecordProcessor databaseRecordProcessor(@Nullable AgileLoggerContext agileLoggerContext,
                                                   AgileLoggerJdbcTemplate agileLoggerJdbcTemplate) {
        return new DatabaseRecordProcessor(agileLoggerContext, agileLoggerJdbcTemplate);
    }

}
