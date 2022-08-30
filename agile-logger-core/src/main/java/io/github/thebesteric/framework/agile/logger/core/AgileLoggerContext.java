package io.github.thebesteric.framework.agile.logger.core;

import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.JoinMethod;
import io.github.thebesteric.framework.agile.logger.core.domain.SyntheticAgileLogger;
import io.github.thebesteric.framework.agile.logger.core.handler.Handler;
import io.github.thebesteric.framework.agile.logger.core.pipeline.Node;
import io.github.thebesteric.framework.agile.logger.core.pipeline.Pipeline;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

import java.lang.reflect.Method;

/**
 * AgileLoggerContext
 *
 * @author Eric Joe
 * @since 1.0
 */
public class AgileLoggerContext {

    private Pipeline<Node<Handler>> pipeline;

    private JoinPoint joinPoint;

    public AgileLoggerContext(Pipeline<Node<Handler>> pipeline, JoinPoint joinPoint) {
        this.pipeline = pipeline;
        this.joinPoint = joinPoint;
    }

    public Class<?> getDeclaringType() {
        return joinPoint.getSignature().getDeclaringType();
    }

    public Method getDeclaringMethod() {
        Class<?> clazz = getDeclaringType();
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] parameterTypes = ((CodeSignature) joinPoint.getSignature()).getParameterTypes();
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }

    public AgileLogger getAgileLogger() {
        Method method = getDeclaringMethod();
        AgileLogger agileLogger = method.getAnnotation(AgileLogger.class);
        if (agileLogger != null) {
            // AgileLogger from method
            return agileLogger;
        }
        // AgileLogger from type
        Class<?> clazz = getDeclaringType();
        return clazz.getAnnotation(AgileLogger.class);
    }

    public SyntheticAgileLogger getSyntheticAgileLogger() {
        AgileLogger agileLoggerOnType = getDeclaringType().getAnnotation(AgileLogger.class);
        AgileLogger agileLoggerOnMethod = getDeclaringMethod().getAnnotation(AgileLogger.class);
        return new SyntheticAgileLogger(agileLoggerOnType, agileLoggerOnMethod);
    }

    public InvokeLog fire(AgileContext context) throws NoSuchMethodException {
        InvokeLog invokeLog = null;
        // For the thread pipeline
        for (Node<Handler> node : pipeline.getNodes()) {
            Handler handler = node.getHandler();
            if (handler != null) {
                if (pipeline.isHead(node)) {
                    invokeLog = new InvokeLog(context.getInvokeLogId());
                }
                if (!pipeline.isTail(node)) {
                    invokeLog = handler.handle(context, new JoinMethod(joinPoint), invokeLog);
                    if (invokeLog == null) {
                        break;
                    }
                }
            }
        }
        context.setInvokeLogId(invokeLog == null ? null : invokeLog.getId());
        return invokeLog;
    }

    public void bomb(AgileContext context, InvokeLog lastInvokeLog) {
        Node<Handler> tail = pipeline.getTail();
        if (AgileContext.async) {
            AgileContext.asyncExecutorService.execute(() -> {
                tail.getHandler().handle(context, new JoinMethod(joinPoint), lastInvokeLog);
            });
        } else {
            tail.getHandler().handle(context, new JoinMethod(joinPoint), lastInvokeLog);
        }
    }

    /* getter and setter */

    public Pipeline<Node<Handler>> getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline<Node<Handler>> pipeline) {
        this.pipeline = pipeline;
    }

    public JoinPoint getJoinPoint() {
        return joinPoint;
    }

    public void setJoinPoint(JoinPoint joinPoint) {
        this.joinPoint = joinPoint;
    }
}
