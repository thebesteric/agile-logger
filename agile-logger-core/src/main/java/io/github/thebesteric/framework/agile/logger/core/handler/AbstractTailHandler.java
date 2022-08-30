package io.github.thebesteric.framework.agile.logger.core.handler;

import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.JoinMethod;

/**
 * AbstractTailHandler
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-10 15:30:04
 */
public abstract class AbstractTailHandler implements Handler {

    @Override
    public InvokeLog handle(AgileContext ctx, JoinMethod joinMethod, InvokeLog invokeLog) {
        process(ctx, joinMethod, invokeLog);
        return null;
    }

    public abstract void process(AgileContext ctx, JoinMethod joinMethod, InvokeLog invokeLog);
}
