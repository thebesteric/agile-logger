package io.github.thebesteric.framework.agile.logger.spring.processor.invoke;

import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;

/**
 * DefaultInvokeLoggerProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
public class DefaultInvokeLoggerProcessor extends AbstractInvokeLoggerProcessor {

    public DefaultInvokeLoggerProcessor(AgileLoggerSpringProperties properties) {
        super(properties);
    }

    @Override
    public InvokeLog doAfterProcessor(InvokeLog invokeLog) {
        return invokeLog;
    }
}
