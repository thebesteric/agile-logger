package io.github.thebesteric.framework.agile.logger.spring.processor.record;

import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.spring.processor.RecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;

import java.util.concurrent.ExecutorService;

/**
 * AbstractThreadPoolRecordProcessor
 *
 * @author Eric Joe
 * @since 1.0
 */
public abstract class AbstractThreadPoolRecordProcessor implements RecordProcessor {

    protected final AgileLoggerContext agileLoggerContext;

    private ExecutorService recordLoggerThreadPool;

    public AbstractThreadPoolRecordProcessor(AgileLoggerContext agileLoggerContext) {
        this.agileLoggerContext = agileLoggerContext;
        if (agileLoggerContext.getProperties().isAsync()) {
            this.recordLoggerThreadPool = agileLoggerContext.getRecordLoggerThreadPool();
        }
    }

    @Override
    public void processor(InvokeLog invokeLog) {
        if (recordLoggerThreadPool != null) {
            recordLoggerThreadPool.execute(() -> {
                doExecute(invokeLog);
            });
        } else {
            doExecute(invokeLog);
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
