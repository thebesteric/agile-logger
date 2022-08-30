package io.github.thebesteric.framework.agile.logger.spring.processor;

import io.github.thebesteric.framework.agile.logger.commons.exception.UnsupportedModeException;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;

/**
 * RecordProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
public interface RecordProcessor {

    boolean supports(LogMode model) throws UnsupportedModeException;

    void processor(InvokeLog invokeLog);

}
