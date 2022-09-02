package io.github.thebesteric.framework.agile.logger.plugin.redis.spring.config;

import io.github.thebesteric.framework.agile.logger.commons.utils.ClassPathScanner;
import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import io.github.thebesteric.framework.agile.logger.spring.config.AbstractAgileLoggerInitialization;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * AgileLoggerRedisInitialization
 *
 * @author Eric Joe
 * @version 1.0
 */
public class AgileLoggerRedisInitialization extends AbstractAgileLoggerInitialization {
    private static final Logger log = LoggerFactory.getLogger(AgileLoggerRedisInitialization.class);

    public AgileLoggerRedisInitialization(AgileLoggerSpringProperties properties, List<ClassPathScanner> classPathScanners) {
        super(properties, classPathScanners);
    }

    @Override
    public void start() {
        if (LogMode.REDIS == properties.getLogMode()) {
            LoggerPrinter.info(log, "Redis plugin installed: {}", properties.getRedis().toString());
        }
    }
}
