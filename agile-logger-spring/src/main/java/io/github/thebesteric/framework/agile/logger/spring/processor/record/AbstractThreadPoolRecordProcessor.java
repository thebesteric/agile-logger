package io.github.thebesteric.framework.agile.logger.spring.processor.record;

import io.github.thebesteric.framework.agile.logger.commons.utils.CurlUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.IOUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.commons.utils.UrlUtils;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.MockInfo;
import io.github.thebesteric.framework.agile.logger.spring.processor.RecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

/**
 * AbstractThreadPoolRecordProcessor
 *
 * @author Eric Joe
 * @since 1.0
 */
@Slf4j
public abstract class AbstractThreadPoolRecordProcessor implements RecordProcessor {

    protected final AgileLoggerContext agileLoggerContext;

    private ExecutorService recordLoggerThreadPool;

    public AbstractThreadPoolRecordProcessor(AgileLoggerContext agileLoggerContext) {
        this.agileLoggerContext = agileLoggerContext;
        if (agileLoggerContext.getProperties().getAsync().isEnable()) {
            this.recordLoggerThreadPool = agileLoggerContext.getRecordLoggerThreadPool();
        }
    }

    @Override
    public void processor(InvokeLog invokeLog) {
        // Set mock identifier
        setMockIdentifier(invokeLog);
        // Record CURL
        setCurl(invokeLog);
        if (recordLoggerThreadPool != null) {
            recordLoggerThreadPool.execute(() -> {
                doExecute(invokeLog);
            });
        } else {
            doExecute(invokeLog);
        }
    }

    /**
     * Set CURL when log is RequestLog
     */
    private void setCurl(InvokeLog invokeLog) {
        if (!this.agileLoggerContext.getProperties().getConfig().getCurl().isEnable()) {
            return;
        }
        if (invokeLog instanceof RequestLog) {
            RequestLog requestLog = (RequestLog) invokeLog;
            String curl = CurlUtils.builder()
                    .url(requestLog.getUrl())
                    .method(requestLog.getMethod())
                    .contentType(requestLog.getContentType())
                    .urlQuery(UrlUtils.queryStringToMap(requestLog.getQuery()))
                    .fromParams(requestLog.getParams())
                    .headers(requestLog.getHeaders())
                    .body(IOUtils.toByteArray(requestLog.getBody())).curl();
            requestLog.setCurl(curl);
            LoggerPrinter.trace(log, curl);
        }
    }

    /**
     * Set Mock Identifier in cascaded
     */
    private void setMockIdentifier(InvokeLog invokeLog) {
        if (!this.agileLoggerContext.getProperties().getConfig().getMock().isEnable()) {
            return;
        }
        MockInfo mockInfo = AgileLoggerContext.getMockInfo();
        if (mockInfo == null && invokeLog.isMock()) {
            AgileLoggerContext.setMockInfo(new MockInfo(invokeLog));
            return;
        }

        String logId = invokeLog.getLogId();
        String logParentId = invokeLog.getLogParentId();

        // mockInfo.getParentId().equals(logId): There is only one method inside the calling method
        // mockInfo.getId().equals(logParentId): There are multiple methods inside the calling method
        if (mockInfo != null && (mockInfo.getParentId().equals(logId) || mockInfo.getId().equals(logParentId))) {
            invokeLog.setMock(true);
            if (logParentId != null && mockInfo.getParentId().equals(logId)) {
                AgileLoggerContext.setMockInfo(new MockInfo(invokeLog));
            } else {
                AgileLoggerContext.setMockInfo(mockInfo);
            }
        } else if (mockInfo != null) {
            AgileLoggerContext.setMockInfo(mockInfo);
        }
    }

    private void doExecute(InvokeLog invokeLog) {
        try {
            doProcess(invokeLog);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public abstract void doProcess(InvokeLog invokeLog) throws Throwable;

}
