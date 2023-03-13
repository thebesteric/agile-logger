package io.github.thebesteric.framework.agile.logger.spring.processor.invoke;

import io.github.thebesteric.framework.agile.logger.commons.utils.DurationWatcher;
import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.TransactionUtils;
import io.github.thebesteric.framework.agile.logger.core.domain.ExecuteInfo;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.SyntheticAgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.domain.SpringSyntheticAgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.processor.InvokeLoggerProcessor;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

/**
 * AbstractInvokeLoggerProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
@RequiredArgsConstructor
public abstract class AbstractInvokeLoggerProcessor implements InvokeLoggerProcessor {

    private final AgileLoggerSpringProperties properties;

    @Override
    public InvokeLog processor(String logId, String parentId, Method method, Object[] args, Object result, String exception, DurationWatcher.Duration duration, boolean mock) {
        // Initialize the invokeLog
        InvokeLog invokeLog = new InvokeLog(logId, parentId);

        SyntheticAgileLogger syntheticAgileLogger = SpringSyntheticAgileLogger.getSpringSyntheticAgileLogger(method);

        // Create InvokeLog
        invokeLog = InvokeLog.builder(invokeLog)
                .trackId(TransactionUtils.get())
                .createdAt(duration.getStartTime())
                .tag(syntheticAgileLogger.getTag())
                .extra(syntheticAgileLogger.getExtra())
                .executeInfo(new ExecuteInfo(method, args, duration))
                .exception(exception)
                .result(result)
                .level(StringUtils.isNotEmpty(exception) ? InvokeLog.LEVEL_ERROR : syntheticAgileLogger.getLevel())
                .mock(mock)
                .build();

        return doAfterProcessor(invokeLog);
    }

    /**
     * Executes when processor is processed
     *
     * @param invokeLog {@link InvokeLog}
     * @return InvokeLog
     */
    public abstract InvokeLog doAfterProcessor(InvokeLog invokeLog);
}
