package io.github.thebesteric.framework.agile.logger.boot.starter.config;

import io.github.thebesteric.framework.agile.logger.commons.utils.*;
import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.utils.DefaultIdGenerator;
import io.github.thebesteric.framework.agile.logger.core.utils.IdGenerator;
import io.github.thebesteric.framework.agile.logger.spring.config.AbstractAgileLoggerInitialization;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AbstractAgileLoggerFilter;
import lombok.extern.slf4j.Slf4j;

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
            LoggerPrinter.info(log, "Logger has been Disabled");
            return;
        }

        String projectPath = ClassPathUtils.getProjectPath();

        // Scanner @Controller and @AgileLogger and so on
        for (ClassPathScanner classPathScanner : classPathScanners) {
            classPathScanner.scan(projectPath, properties.getCompilePaths());
        }

        // Set idGenerator & trackIdGenerator
        AgileContext.idGenerator = getBeanOrDefault(ID_GENERATOR_BEAN_NAME, IdGenerator.class, DefaultIdGenerator.getInstance());
        AgileContext.trackIdGenerator = getBeanOrDefault(TRACK_ID_GENERATOR_BEAN_NAME, IdGenerator.class, DefaultIdGenerator.getInstance());

        // Print mapping between print urls and methods
        if (log.isTraceEnabled()) {
            LoggerPrinter.debug(log, "Scan project path is {}", projectPath);
            AbstractAgileLoggerFilter.URL_MAPPING.forEach((url, method) -> LoggerPrinter.debug(log, "Mapping: {} => {}", url, SignatureUtils.methodSignature(method)));
        }

        AgileLoggerSpringProperties.Config config = properties.getConfig();
        AgileLoggerSpringProperties.Mock mock = config.getMock();
        AgileLoggerSpringProperties.Track track = config.getTrack();
        AgileLoggerSpringProperties.Version version = config.getVersion();

        AgileLoggerSpringProperties.Async async = properties.getAsync();
        String asyncMessage = async.isEnable() ? "Async: " + async.getAsyncParams() : "Sync";
        String traceMessage = track.isUseSkyWalkingTrace() ? "SkyWalking Trace" : "Local Generator";
        LoggerPrinter.info(log, "Log Mode is {}, Running in {}, TrackIdGenerator: {}", properties.getLogMode(), asyncMessage, traceMessage);


        String mockStatus = mock.isEnable() ? mock.toString() : "Disabled";
        String trackIdName = StringUtils.isNotEmpty(track.getName()) ? track.getName() : "USE_DEFAULT";
        String versionName = StringUtils.isNotEmpty(version.getName()) ? version.getName() : "USE_DEFAULT";
        LoggerPrinter.info(log, "Config: mock is {}, version name is: {}, track-id name is: {}", mockStatus, versionName, trackIdName);
    }
}
