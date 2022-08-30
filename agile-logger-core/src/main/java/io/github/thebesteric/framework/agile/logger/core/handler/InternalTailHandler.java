package io.github.thebesteric.framework.agile.logger.core.handler;

import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.domain.AbstractEntity;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.JoinMethod;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TailHandler
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-27 22:21:39
 */
public class InternalTailHandler extends AbstractTailHandler {

    private static final Logger log = LoggerFactory.getLogger(InternalTailHandler.class);

    @Override
    public void process(AgileContext ctx, JoinMethod joinMethod, InvokeLog invokeLog) {
        invokeLog.setThreadName(Thread.currentThread().getName());
        if (LogMode.LOG.equals(AgileContext.logMode)) {
            handleLogMode(invokeLog);
        } else {
            handleStdoutMode(invokeLog);
        }
    }

    private void handleStdoutMode(InvokeLog invokeLog) {
        switch (invokeLog.getLevel()) {
            case AbstractEntity.LEVEL_DEBUG:
            case AbstractEntity.LEVEL_INFO:
                System.out.println(invokeLog.print());
                break;
            default:
                System.err.println(invokeLog.print());
        }
    }

    private void handleLogMode(InvokeLog invokeLog) {
        switch (invokeLog.getLevel()) {
            case AbstractEntity.LEVEL_INFO:
                log.info(invokeLog.print());
                break;
            case AbstractEntity.LEVEL_WARN:
                log.warn(invokeLog.print());
                break;
            case AbstractEntity.LEVEL_ERROR:
                log.error(invokeLog.print());
                break;
            default:
                log.debug(invokeLog.print());
        }
    }
}
