package io.github.thebesteric.framework.agile.logger.spring.wrapper;

import io.github.thebesteric.framework.agile.logger.commons.utils.*;
import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.utils.DefaultIdGenerator;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.processor.*;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * AbstractAgileLoggerFilter
 *
 * @author Eric Joe
 * @since 1.0
 */
@Slf4j
public abstract class AbstractAgileLoggerFilter implements Filter {

    public static final Map<String, Method> URL_MAPPING = new ConcurrentHashMap<>(128);
    public static final Map<String, Method> PATH_VARIABLE_URL_MAPPING = new HashMap<>(128);
    public static boolean useSkyWalkingTrace = true;

    protected final AgileLoggerContext agileLoggerContext;

    protected final AgileLoggerSpringProperties properties;
    protected final IgnoreUriProcessor ignoreUriProcessor;
    protected final IgnoreMethodProcessor ignoreMethodProcessor;
    protected final RequestLoggerProcessor requestLoggerProcessor;
    protected final InvokeLoggerProcessor invokeLoggerProcessor;

    protected final RecordProcessor currentRecordProcessor;

    public AbstractAgileLoggerFilter(AgileLoggerContext agileLoggerContext, List<RecordProcessor> recordProcessors) {
        this.agileLoggerContext = agileLoggerContext;
        this.properties = agileLoggerContext.getProperties();
        this.ignoreUriProcessor = agileLoggerContext.getIgnoreUriProcessor();
        this.ignoreMethodProcessor = agileLoggerContext.getIgnoreMethodProcessor();
        this.requestLoggerProcessor = agileLoggerContext.getRequestLoggerProcessor();
        this.invokeLoggerProcessor = agileLoggerContext.getInvokeLoggerProcessor();
        this.currentRecordProcessor = agileLoggerContext.setRecordProcessors(recordProcessors);
    }

    protected boolean checkLegalUri(String targetUri) {
        return doCheckLegalUri(targetUri, true) && doCheckLegalUri(targetUri, false);
    }

    public boolean doCheckLegalUri(String targetUrl, boolean flag) {
        boolean passed = false;
        String[] urls = flag ? properties.getUrlFilter().getIncludes() : properties.getUrlFilter().getExcludes();
        if (urls != null && urls.length > 0) {
            boolean[] results = new boolean[urls.length];
            for (int i = 0; i < urls.length; i++) {
                results[i] = passed = Pattern.matches(urls[i], targetUrl);
                if (passed && flag) {
                    break;
                }
            }
            if (!flag) {
                Set<Boolean> resultSet = new HashSet<>();
                for (boolean result : results) {
                    resultSet.add(result);
                }
                // The passed is true as long as one element in resultSet is false.
                passed = resultSet.contains(false);
            }
        }
        return CollectionUtils.isEmpty(urls) || passed;
    }

    protected void initConfigProperties(AgileLoggerRequestWrapper requestWrapper) {
        if (AgileContext.trackIdGenerator == null) {
            AgileContext.trackIdGenerator = DefaultIdGenerator.getInstance();
        }

        AgileLoggerSpringProperties.Config config = this.properties.getConfig();
        AgileLoggerSpringProperties.Track track = config.getTrack();
        AgileLoggerSpringProperties.Version version = config.getVersion();

        if (track.isUseSkyWalkingTrace() && useSkyWalkingTrace) {
            if (StringUtils.isEmpty(TraceContext.traceId()) || "Ignored_Trace".equalsIgnoreCase(TraceContext.traceId())) {
                LoggerPrinter.warn(log, "Please check Sky Walking agent setting are correct or OAP Server are running that the local track id will be used instead");
                LoggerPrinter.warn(log, "Make sure add VM options, " +
                        "Example: -javaagent:/opt/skywalking-agent.jar -Dskywalking.agent.service_name=app -Dskywalking.collector.backend_service=127.0.0.1:11800");
                TransactionUtils.initialize(AgileContext.trackIdGenerator.generate());
                useSkyWalkingTrace = false;
            } else {
                TransactionUtils.set(TraceContext.traceId());
            }
        } else {
            TransactionUtils.initialize(AgileContext.trackIdGenerator.generate());
            useSkyWalkingTrace = false;
        }

        // Specify the track-id name
        if (track.getName() != null) {
            TransactionUtils.TRACK_ID_NAMES.add(0, track.getName());
        }

        // Specify the version name
        if (version.getName() != null) {
            VersionUtils.VERSION_NAMES.add(0, version.getName());
        }

        // Find default value for config properties
        Enumeration<String> headerNames = requestWrapper.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = requestWrapper.getHeader(headerName);
            if (TransactionUtils.hasTrackId(headerName)) {
                TransactionUtils.set(headerValue);
            }
            if (VersionUtils.hasVersion(headerName)) {
                VersionUtils.set(headerValue);
            }
        }
    }

}
