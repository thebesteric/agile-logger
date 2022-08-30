package io.github.thebesteric.framework.agile.logger.core;

import io.github.thebesteric.framework.agile.logger.core.pipeline.Pipeline;
import io.github.thebesteric.framework.agile.logger.core.utils.DefaultIdGenerator;
import org.aspectj.lang.JoinPoint;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * AgileContext
 *
 * @author Eric Joe
 * @since 1.0
 */
public class AgileContext extends AgileLoggerProperties {

    private String trackId;
    private String invokeLogId;
    private final Map<JoinPoint, AgileLoggerContext> loggerContexts;

    public AgileContext() {
        this.trackId = generateTrackId();
        this.loggerContexts = new LinkedHashMap<>();
    }

    public AgileLoggerContext getAgileLoggerContext(JoinPoint joinPoint) {
        return loggerContexts.get(joinPoint);
    }

    public Map<JoinPoint, AgileLoggerContext> getAgileLoggerContexts() {
        return loggerContexts;
    }

    public Set<JoinPoint> getAgileLoggerContextKeys() {
        return loggerContexts.keySet();
    }

    public List<AgileLoggerContext> getAgileLoggerContextValues() {
        return new ArrayList<>(loggerContexts.values());
    }

    public String generateTrackId() {
        if (trackIdGenerator == null) {
            trackIdGenerator = DefaultIdGenerator.getInstance();
        }
        return trackIdGenerator.generate();
    }

    /**
     * Shutdown thread pool in async mode
     */
    public void shutdownExecutorService() {
        if (AgileContext.async && AgileContext.asyncExecutorService != null) {
            try {
                AgileContext.asyncExecutorService.shutdown();
                while (!AgileContext.asyncExecutorService.isTerminated()) {
                    if (AgileContext.asyncExecutorService.awaitTermination(AgileContext.asyncAwaitTimeout, TimeUnit.SECONDS)) {
                        break;
                    }
                }
                AgileContext.asyncExecutorService.shutdownNow();
            } catch (Exception e) {
                e.printStackTrace();
                AgileContext.asyncExecutorService.shutdownNow();
            }
        }
    }

    /* getter and setter */

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getInvokeLogId() {
        return invokeLogId;
    }

    public void setInvokeLogId(String invokeLogId) {
        this.invokeLogId = invokeLogId;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }
}
