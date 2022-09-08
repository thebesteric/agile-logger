package io.github.thebesteric.framework.agile.logger.boot.starter.config;

import io.github.thebesteric.framework.agile.logger.commons.utils.ClassPathScanner;
import io.github.thebesteric.framework.agile.logger.commons.utils.ClassPathUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.ClassUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.utils.DefaultIdGenerator;
import io.github.thebesteric.framework.agile.logger.core.utils.IdGenerator;
import io.github.thebesteric.framework.agile.logger.spring.config.AbstractAgileLoggerInitialization;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AbstractAgileLoggerFilter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * AgileLoggerInitialization
 *
 * @author Eric Joe
 * @since 1.0
 */
@Slf4j
public class AgileLoggerInitialization extends AbstractAgileLoggerInitialization {

    public AgileLoggerInitialization(AgileLoggerSpringProperties properties, List<ClassPathScanner> classPathScanners) {
        super(properties, classPathScanners);
    }

    @Override
    public void start() {
        if (!properties.isEnable()) {
            LoggerPrinter.info(log, "Agile Logger disabled");
            return;
        }

        String projectPath = ClassPathUtils.getProjectPath();

        // Scanner @Controller and @SwitchLogger and so on
        for (ClassPathScanner classPathScanner : classPathScanners) {
            classPathScanner.doScan(new File(projectPath + "/"), properties.getCompilePaths());
        }

        // Set idGenerator & trackIdGenerator
        AgileContext.idGenerator = getBeanOrDefault(ID_GENERATOR_BEAN_NAME, IdGenerator.class, DefaultIdGenerator.getInstance());
        AgileContext.trackIdGenerator = getBeanOrDefault(TRACK_ID_GENERATOR_BEAN_NAME, IdGenerator.class, DefaultIdGenerator.getInstance());

        // Print mapping between print urls and methods
        if (log.isTraceEnabled()) {
            LoggerPrinter.debug(log, "Scan project path is {}", projectPath);
            AbstractAgileLoggerFilter.URL_MAPPING.forEach((url, method) -> LoggerPrinter.debug(log, "Mapping: {} => {}", url, ClassUtils.getMethodQualifiedName(method)));
        }

        LoggerPrinter.info(log, "Log Mode is {}, Running in {}",
                properties.getLogMode(), properties.isAsync() ? "Async: " + properties.getAsyncParams() : "Sync");
    }
}
