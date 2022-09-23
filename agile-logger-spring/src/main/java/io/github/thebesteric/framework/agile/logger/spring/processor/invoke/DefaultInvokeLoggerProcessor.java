package io.github.thebesteric.framework.agile.logger.spring.processor.invoke;

import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;

/**
 * DefaultInvokeLoggerProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
public class DefaultInvokeLoggerProcessor extends AbstractInvokeLoggerProcessor {
    @Override
    public InvokeLog doAfterProcessor(InvokeLog invokeLog) {
        return invokeLog;
    }
}
