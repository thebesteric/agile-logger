package io.github.thebesteric.framework.agile.logger.spring.processor.request;

import io.github.thebesteric.framework.agile.logger.commons.utils.DurationWatcher;
import io.github.thebesteric.framework.agile.logger.commons.utils.JsonUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.core.domain.ExecuteInfo;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;
import io.github.thebesteric.framework.agile.logger.spring.processor.RequestLoggerProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerRequestWrapper;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerResponseWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * AbstractRequestLoggerProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
@Slf4j
public abstract class AbstractRequestLoggerProcessor implements RequestLoggerProcessor {

    @Override
    public RequestLog processor(String id, AgileLoggerRequestWrapper requestWrapper, AgileLoggerResponseWrapper responseWrapper, DurationWatcher.Duration duration) throws IOException {
        RequestLog requestLog = new RequestLog(id, requestWrapper, responseWrapper, duration);
        try {
            if (requestLog.getResult() != null) {
                requestLog.setResult(JsonUtils.mapper.readTree(requestLog.getResult().toString()));
            }
        } catch (Exception ex) {
            LoggerPrinter.error(log, "Cannot parse {} to json", requestLog.getResult());
        }
        Method method = getMethod(requestLog.getUri());
        if (method != null) {
            buildSyntheticAgileLogger(method, requestLog);
            if (responseWrapper.getException() != null) {
                requestLog.setLevel(InvokeLog.LEVEL_ERROR);
            }
            requestLog.setExecuteInfo(new ExecuteInfo(method, null, duration));
        }
        return doAfterProcessor(requestLog);
    }

    /**
     * Executes when processor is processed
     *
     * @param requestLog {@link RequestLog}
     * @return RequestLog
     */
    public abstract RequestLog doAfterProcessor(RequestLog requestLog);
}
