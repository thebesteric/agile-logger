package io.github.thebesteric.framework.agile.logger.plugin.database.spring.config;

import io.github.thebesteric.framework.agile.logger.commons.utils.ClassPathScanner;
import io.github.thebesteric.framework.agile.logger.commons.utils.CollectionUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import io.github.thebesteric.framework.agile.logger.plugin.database.spring.jdbc.AgileLoggerJdbcTemplate;
import io.github.thebesteric.framework.agile.logger.spring.config.AbstractAgileLoggerInitialization;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * AgileLoggerMySQLInitialization
 *
 * @author Eric Joe
 * @version 1.0
 */
public class AgileLoggerDatabaseInitialization extends AbstractAgileLoggerInitialization {

    private static final Logger log = LoggerFactory.getLogger(AgileLoggerDatabaseInitialization.class);

    public AgileLoggerDatabaseInitialization(AgileLoggerSpringProperties properties, List<ClassPathScanner> classPathScanners) {
        super(properties, classPathScanners);
    }

    @Override
    public void start() {
        if (LogMode.DATABASE == properties.getLogMode()) {
            String url = properties.getDatabase().getUrl();
            String vendor = getDatabaseVendor(url);
            if (vendor == null) {
                throw new IllegalArgumentException("Unsupported database url: " + url);
            }
            LoggerPrinter.info(log, "Database plugin ({}) installed: {}", vendor, properties.getDatabase().toString());

            AgileLoggerJdbcTemplate agileLoggerJdbcTemplate = getBean(AgileLoggerJdbcTemplate.class);
            String tableNamePrefix = properties.getDatabase().getTableNamePrefix();
            try {
                agileLoggerJdbcTemplate.createOrUpdateTable(tableNamePrefix, CollectionUtils.createList(InvokeLog.class, RequestLog.class));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private String getDatabaseVendor(String url) {
        String vendor = null;
        if (StringUtils.isNotEmpty(url)) {
            if (url.startsWith("jdbc:mysql")) {
                vendor = "MySQL";
            } else if (url.startsWith("jdbc:hsqldb")) {
                vendor = "HsqlDB";
            } else if (url.startsWith("jdbc:postgresql")) {
                vendor = "Postgresql";
            } else if (url.startsWith("jdbc:oracle")) {
                vendor = "Oracle";
            } else if (url.startsWith("jdbc:jtds")) {
                vendor = "JTDS";
            } else if (url.startsWith("jdbc:sqlserver")) {
                vendor = "SqlServer";
            } else if (url.startsWith("jdbc:odbc")) {
                vendor = "ODBC";
            }
        }
        return vendor;
    }
}
