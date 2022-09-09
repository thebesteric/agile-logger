package io.github.thebesteric.framework.agile.logger.spring.processor.request;

import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;
import lombok.extern.slf4j.Slf4j;

/**
 * DefaultRequestLoggerProcessor
 *
 * @author Eric Joe
 * @since 1.0
 */
@Slf4j
public class DefaultRequestLoggerProcessor extends AbstractRequestLoggerProcessor {

    @Override
    public RequestLog doAfterProcessor(RequestLog requestLog) {
        return requestLog;
    }
}