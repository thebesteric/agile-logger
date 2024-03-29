package io.github.thebesteric.framework.agile.logger.spring.processor;

import io.github.thebesteric.framework.agile.logger.commons.utils.DurationWatcher;
import io.github.thebesteric.framework.agile.logger.core.domain.AbstractEntity;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.SyntheticAgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;
import io.github.thebesteric.framework.agile.logger.spring.domain.SpringSyntheticAgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AbstractAgileLoggerFilter;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerRequestWrapper;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerResponseWrapper;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * RequestLoggerProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
public interface RequestLoggerProcessor {

    RequestLog processor(String id, AgileLoggerRequestWrapper requestWrapper, AgileLoggerResponseWrapper responseWrapper, DurationWatcher.Duration duration) throws IOException;

    default void buildSyntheticAgileLogger(Method method, InvokeLog invokeLog) {
        SyntheticAgileLogger syntheticAgileLogger = SpringSyntheticAgileLogger.getSpringSyntheticAgileLogger(method);
        invokeLog.setLevel(syntheticAgileLogger.getLevel());
        invokeLog.setExtra(syntheticAgileLogger.getExtra());
        invokeLog.setTag(AbstractEntity.TAG_DEFAULT);
    }

    default Method getMethod(String uri) {
        return AbstractAgileLoggerFilter.URL_MAPPING.get(uri);
    }
}
