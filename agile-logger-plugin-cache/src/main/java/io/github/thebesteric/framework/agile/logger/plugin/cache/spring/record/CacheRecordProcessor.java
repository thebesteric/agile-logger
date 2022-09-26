package io.github.thebesteric.framework.agile.logger.plugin.cache.spring.record;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.thebesteric.framework.agile.logger.commons.exception.UnsupportedModeException;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import io.github.thebesteric.framework.agile.logger.spring.processor.record.AbstractThreadPoolRecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;

/**
 * CacheRecordProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
public class CacheRecordProcessor extends AbstractThreadPoolRecordProcessor {

    private final Cache<String, Object> cache;

    public CacheRecordProcessor(AgileLoggerContext agileLoggerContext, Cache<String, Object> cache) {
        super(agileLoggerContext);
        this.cache = cache;
    }

    @Override
    public boolean supports(LogMode model) throws UnsupportedModeException {
        return model != null && !model.getName().trim().equals("") && LogMode.CACHE.getName().equalsIgnoreCase(model.getName());
    }

    @Override
    public LogMode getLogMode() {
        return LogMode.CACHE;
    }

    @Override
    public void doProcess(InvokeLog invokeLog) throws Throwable {
        this.cache.put(invokeLog.getLogId(), invokeLog);
    }
}
