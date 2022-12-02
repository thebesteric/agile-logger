package io.github.thebesteric.framework.agile.logger.spring.processor.record;

import io.github.thebesteric.framework.agile.logger.commons.exception.UnsupportedModeException;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * LogRecordProcessor
 *
 * @author Eric Joe
 * @since 1.0
 */
@Slf4j
public class LogRecordProcessor extends AbstractThreadPoolRecordProcessor {

    public LogRecordProcessor(AgileLoggerContext agileLoggerContext) {
        super(agileLoggerContext);
    }

    @Override
    public boolean supports(LogMode model) throws UnsupportedModeException {
        return model != null && !model.getName().trim().equals("") && LogMode.LOG.getName().equalsIgnoreCase(model.getName());
    }

    @Override
    public LogMode getLogMode() {
        return LogMode.LOG;
    }

    @Override
    public void doProcess(InvokeLog invokeLog) throws Throwable {
        switch (invokeLog.getLevel().toUpperCase()) {
            case InvokeLog.LEVEL_INFO:
                log.info(invokeLog.print());
                break;
            case InvokeLog.LEVEL_WARN:
                log.warn(invokeLog.print());
                break;
            case InvokeLog.LEVEL_ERROR:
                log.error(invokeLog.print());
                break;
            default:
                log.debug(invokeLog.print());
        }
    }
}
