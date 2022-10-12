package io.github.thebesteric.framework.agile.logger.spring.processor.request;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.thebesteric.framework.agile.logger.spring.domain.MetricsRequestLog;
import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;

/**
 * MetricsRequestLoggerProcessor
 * <p>This command is used to collect statistics about interface access parameters
 *
 * @author Eric Joe
 * @version 1.0
 */
public class MetricsRequestLoggerProcessor extends AbstractRequestLoggerProcessor {

    public Cache<String, MetricsRequestLog.Metrics> cache;

    public MetricsRequestLoggerProcessor() {
        this(256, 1024);
    }

    public MetricsRequestLoggerProcessor(int initialCapacity, int maximumSize) {
        this.cache = Caffeine.newBuilder().initialCapacity(initialCapacity).maximumSize(maximumSize).build();
    }

    @Override
    public RequestLog doAfterProcessor(RequestLog requestLog) {
        MetricsRequestLog.Metrics metrics = cache.getIfPresent(requestLog.getUri());
        if (metrics == null) {
            metrics = new MetricsRequestLog.Metrics();
        }
        metrics.calc(requestLog);
        cache.put(requestLog.getUri(), metrics);
        return new MetricsRequestLog(requestLog, metrics);
    }
}
