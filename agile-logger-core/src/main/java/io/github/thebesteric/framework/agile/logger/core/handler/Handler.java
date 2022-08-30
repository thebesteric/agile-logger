package io.github.thebesteric.framework.agile.logger.core.handler;

import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.JoinMethod;

/**
 * Handler
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-27 10:46:54
 */
public interface Handler {
    InvokeLog handle(AgileContext ctx, JoinMethod joinMethod, InvokeLog invokeLog);
}
