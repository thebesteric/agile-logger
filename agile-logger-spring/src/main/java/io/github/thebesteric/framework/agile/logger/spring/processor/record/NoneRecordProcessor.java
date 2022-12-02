package io.github.thebesteric.framework.agile.logger.spring.processor.record;

import io.github.thebesteric.framework.agile.logger.commons.exception.UnsupportedModeException;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import io.github.thebesteric.framework.agile.logger.spring.processor.RecordProcessor;

public class NoneRecordProcessor implements RecordProcessor {
    @Override
    public boolean supports(LogMode model) throws UnsupportedModeException {
        return model != null && !model.getName().trim().equals("") && LogMode.NONE.getName().equalsIgnoreCase(model.getName());
    }

    @Override
    public void processor(InvokeLog invokeLog) {
    }

    @Override
    public LogMode getLogMode() {
        return LogMode.NONE;
    }
}
