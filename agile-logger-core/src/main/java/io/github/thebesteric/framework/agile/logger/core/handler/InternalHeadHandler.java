package io.github.thebesteric.framework.agile.logger.core.handler;

import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.JoinMethod;

/**
 * HeadHandler
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-27 22:20:20
 */
public class InternalHeadHandler extends AbstractHeadHandler {

    @Override
    public InvokeLog process(AgileContext ctx, JoinMethod joinMethod, InvokeLog invokeLog) {
        return invokeLog;
    }
}
