package io.github.thebesteric.framework.agile.logger.spring.processor.record;

import io.github.thebesteric.framework.agile.logger.commons.exception.UnsupportedModeException;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;

/**
 * StdoutRecordProcessor
 *
 * @author Eric Joe
 * @since 1.0
 */
public class StdoutRecordProcessor extends AbstractThreadPoolRecordProcessor {

    public StdoutRecordProcessor(AgileLoggerContext agileLoggerContext) {
        super(agileLoggerContext);
    }

    @Override
    public boolean supports(LogMode model) throws UnsupportedModeException {
        return model != null && !model.getName().trim().equals("") && LogMode.STDOUT.getName().equalsIgnoreCase(model.getName());
    }

    @Override
    public void doProcess(InvokeLog invokeLog) {
        switch (invokeLog.getLevel()) {
            case InvokeLog.LEVEL_ERROR:
            case InvokeLog.LEVEL_WARN:
                System.err.println(invokeLog.print());
                break;
            default:
                System.out.println(invokeLog.print());
        }
    }
}
