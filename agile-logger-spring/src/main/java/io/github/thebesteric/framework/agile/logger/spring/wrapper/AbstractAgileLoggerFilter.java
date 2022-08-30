package io.github.thebesteric.framework.agile.logger.spring.wrapper;

import io.github.thebesteric.framework.agile.logger.commons.utils.CollectionUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.utils.DefaultIdGenerator;
import io.github.thebesteric.framework.agile.logger.spring.TransactionUtils;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.processor.IgnoreUriProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.RecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.RequestLoggerProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

import javax.servlet.Filter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
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
    public static boolean showSkyWalkingWarnTips = true;

    protected final AgileLoggerContext agileLoggerContext;

    protected final AgileLoggerSpringProperties properties;
    protected final IgnoreUriProcessor ignoreUriProcessor;
    protected final RequestLoggerProcessor requestLoggerProcessor;

    protected final RecordProcessor currentRecordProcessor;

    public AbstractAgileLoggerFilter(AgileLoggerContext agileLoggerContext, List<RecordProcessor> recordProcessors) {
        this.agileLoggerContext = agileLoggerContext;
        this.properties = agileLoggerContext.getProperties();
        this.ignoreUriProcessor = agileLoggerContext.getIgnoreUriProcessor();
        this.requestLoggerProcessor = agileLoggerContext.getRequestLoggerProcessor();
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
                for (boolean result : results) {
                    if (result) {
                        passed = false;
                        break;
                    }
                }
            }
        }
        return CollectionUtils.isEmpty(urls) || passed;
    }

    protected void initTrackId(AgileLoggerRequestWrapper requestWrapper, boolean isSkyWalkingTrace) {
        if (AgileContext.trackIdGenerator == null) {
            AgileContext.trackIdGenerator = DefaultIdGenerator.getInstance();
        }
        if (isSkyWalkingTrace) {
            if (showSkyWalkingWarnTips && (StringUtils.isEmpty(TraceContext.traceId()) || "Ignored_Trace".equalsIgnoreCase(TraceContext.traceId()))) {
                LoggerPrinter.warn(log, "Please check Sky Walking agent setting are correct or OAP Server are running that the local track id will be used instead");
                TransactionUtils.initialize(AgileContext.trackIdGenerator.generate());
            } else {
                TransactionUtils.set(TraceContext.traceId());
            }
            showSkyWalkingWarnTips = false;
        } else {
            TransactionUtils.initialize(AgileContext.trackIdGenerator.generate());
        }
        Enumeration<String> headerNames = requestWrapper.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = requestWrapper.getHeader(headerName);
            if (TransactionUtils.hasTrackId(headerName)) {
                TransactionUtils.set(headerValue);
            }
        }
    }


}
