package io.github.thebesteric.framework.agile.logger.plugin.cache.spring.config;

import io.github.thebesteric.framework.agile.logger.commons.utils.ClassPathScanner;
import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import io.github.thebesteric.framework.agile.logger.spring.config.AbstractAgileLoggerInitialization;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * AgileLoggerCacheInitialization
 *
 * @author Eric Joe
 * @version 1.0
 */
public class AgileLoggerCacheInitialization extends AbstractAgileLoggerInitialization {

    private static final Logger log = LoggerFactory.getLogger(AgileLoggerCacheInitialization.class);

    public AgileLoggerCacheInitialization(AgileLoggerSpringProperties properties, List<ClassPathScanner> classPathScanners) {
        super(properties, classPathScanners);
    }

    @Override
    public void start() {
        if (LogMode.CACHE == properties.getLogMode()) {
            AgileLoggerSpringProperties.Plugins plugins = properties.getPlugins();
            LoggerPrinter.info(log, "Cache plugin installed: {}", plugins.getCache().toString());
        }
    }
}
