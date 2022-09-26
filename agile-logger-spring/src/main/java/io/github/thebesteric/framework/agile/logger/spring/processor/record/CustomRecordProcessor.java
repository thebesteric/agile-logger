package io.github.thebesteric.framework.agile.logger.spring.processor.record;

import io.github.thebesteric.framework.agile.logger.commons.exception.UnsupportedModeException;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;

/**
 * CustomRecordProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
public abstract class CustomRecordProcessor extends AbstractThreadPoolRecordProcessor {

    public CustomRecordProcessor(AgileLoggerContext agileLoggerContext) {
        super(agileLoggerContext);
        System.out.println("CustomRecordProcessor===========");
    }

    @Override
    public boolean supports(LogMode model) throws UnsupportedModeException {
        return model == null || model.getName().trim().equals("") || LogMode.CUSTOM.getName().equalsIgnoreCase(model.getName());
    }

    @Override
    public LogMode getLogMode() {
        return LogMode.CUSTOM;
    }

    @Override
    public abstract void doProcess(InvokeLog invokeLog) throws Throwable;
}
