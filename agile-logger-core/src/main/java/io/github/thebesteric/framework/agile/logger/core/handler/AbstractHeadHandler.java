package io.github.thebesteric.framework.agile.logger.core.handler;

import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.AgileLoggerContext;
import io.github.thebesteric.framework.agile.logger.core.domain.ExecuteInfo;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.JoinMethod;
import io.github.thebesteric.framework.agile.logger.core.domain.SyntheticAgileLogger;

/**
 * AbstractHeadHandler
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-10 15:41:59
 */
public abstract class AbstractHeadHandler implements Handler {

    @Override
    public InvokeLog handle(AgileContext ctx, JoinMethod joinMethod, InvokeLog invokeLog) {
        this.initInvokeLog(ctx, joinMethod, invokeLog);
        return process(ctx, joinMethod, invokeLog);
    }

    public void initInvokeLog(AgileContext ctx, JoinMethod joinMethod, InvokeLog invokeLog) {
        AgileLoggerContext loggerContext = ctx.getAgileLoggerContext(joinMethod.getJoinPoint());
        SyntheticAgileLogger syntheticAgileLogger = loggerContext.getSyntheticAgileLogger();
        invokeLog.setTrackId(ctx.getTrackId());
        invokeLog.setPrefix(syntheticAgileLogger.getPrefix());
        invokeLog.setTag(syntheticAgileLogger.getTag());
        invokeLog.setLevel(syntheticAgileLogger.getLevel());
        invokeLog.setExtra(syntheticAgileLogger.getExtra());
        ExecuteInfo executeInfo = new ExecuteInfo(loggerContext.getDeclaringMethod(), loggerContext.getJoinPoint().getArgs());
        invokeLog.setExecuteInfo(executeInfo);
    }

    public abstract InvokeLog process(AgileContext ctx, JoinMethod joinMethod, InvokeLog invokeLog);

}
