package io.github.thebesteric.framework.agile.logger.core;

import io.github.thebesteric.framework.agile.logger.core.handler.Handler;
import io.github.thebesteric.framework.agile.logger.core.handler.InternalHeadHandler;
import io.github.thebesteric.framework.agile.logger.core.handler.InternalTailHandler;
import io.github.thebesteric.framework.agile.logger.core.pipeline.DefaultPipeline;
import io.github.thebesteric.framework.agile.logger.core.pipeline.Node;
import io.github.thebesteric.framework.agile.logger.core.pipeline.Pipeline;
import org.aspectj.lang.JoinPoint;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * AgileContextUtils
 *
 * @author Eric Joe
 * @since 1.0
 */
public class AgileContextUtils {

    public static ThreadLocal<AgileContext> threadLocal = new ThreadLocal<>();

    public static AgileContext create(JoinPoint joinPoint) {
        AgileContext agileContext = threadLocal.get();
        if (agileContext == null) {
            agileContext = new AgileContext();
        }
        AgileLoggerContext loggerContext = agileContext.getAgileLoggerContext(joinPoint);
        if (loggerContext == null) {
            Pipeline<Node<Handler>> pipeline = AgileContext.pipeline == null ? AgileContextUtils.createDefaultPipeline() : AgileContext.pipeline;
            loggerContext = new AgileLoggerContext(pipeline, joinPoint);
            agileContext.getAgileLoggerContexts().put(joinPoint, loggerContext);
            threadLocal.set(agileContext);
        }
        return agileContext;
    }

    public static String getTrackId() {
        return threadLocal.get() == null ? null : threadLocal.get().getTrackId();
    }

    public static AgileContext getAgileContext() {
        return threadLocal.get();
    }

    public static Pipeline<Node<Handler>> createDefaultPipeline() {
        Node<Handler> internalHead = new Node<>("internalHead", new InternalHeadHandler());
        Node<Handler> internalTail = new Node<>("internalTail", new InternalTailHandler());
        return new DefaultPipeline(internalHead, internalTail);
    }

    public static Pipeline<Node<Handler>> getDefaultPipeline() {
        return new DefaultPipeline();
    }

    public static void createPipeline(Pipeline<Node<Handler>> pipeline) {
        AgileContext.pipeline = pipeline;
    }

    public static Pipeline<Node<Handler>> getPipeline() {
        return AgileContext.pipeline;
    }

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return (ThreadPoolExecutor) AgileContext.asyncExecutorService;
    }

}
