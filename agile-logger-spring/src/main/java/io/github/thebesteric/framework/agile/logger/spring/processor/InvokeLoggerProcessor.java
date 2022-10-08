package io.github.thebesteric.framework.agile.logger.spring.processor;

import io.github.thebesteric.framework.agile.logger.commons.utils.DurationWatcher;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;

import java.lang.reflect.Method;

/**
 * InvokeLoggerProcessor
 *
 * @author Eric Joe
 * @since 1.0
 */
public interface InvokeLoggerProcessor {
    InvokeLog processor(String logId, String parentId, Method method, Object[] args, Object result, String exception, DurationWatcher.Duration duration, boolean mock);
}
