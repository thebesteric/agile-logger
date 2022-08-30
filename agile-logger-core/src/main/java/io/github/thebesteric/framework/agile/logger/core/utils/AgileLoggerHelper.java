package io.github.thebesteric.framework.agile.logger.core.utils;

import io.github.thebesteric.framework.agile.logger.commons.exception.InvalidDataException;
import io.github.thebesteric.framework.agile.logger.commons.exception.PipelineDoesNotHaveEnoughNodesException;
import io.github.thebesteric.framework.agile.logger.commons.exception.PipelineNotDefinedException;
import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.AgileContextUtils;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import io.github.thebesteric.framework.agile.logger.core.handler.Handler;
import io.github.thebesteric.framework.agile.logger.core.pipeline.Node;
import io.github.thebesteric.framework.agile.logger.core.pipeline.Pipeline;
import io.github.thebesteric.framework.agile.logger.core.plugin.AgileLoggerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * AgileLoggerHelper
 * Just wrapped AgileContextUtils for easy build initialization of the environment
 *
 * @author Eric Joe
 * @version 1.0
 * @see AgileContextUtils
 * @since 2022-08-05 13:17:11
 */
public class AgileLoggerHelper {

    private static final Logger log = LoggerFactory.getLogger(AgileLoggerHelper.class);

    private AgileLoggerHelper() {
        super();
    }

    public static Builder builder() {
        return new AgileLoggerHelper.Builder();
    }

    public static class Builder {

        private Pipeline<Node<Handler>> pipeline;

        public Builder enable(boolean enable) {
            AgileContext.enable = enable;
            return this;
        }

        public Builder logMode(LogMode logMode) {
            AgileContext.logMode = logMode;
            return this;
        }

        public Builder idGenerator(IdGenerator idGenerator) {
            AgileContext.idGenerator = idGenerator;
            return this;
        }

        public Builder trackIdGenerator(IdGenerator idGenerator) {
            AgileContext.trackIdGenerator = idGenerator;
            return this;
        }

        public Builder createPipeline(Pipeline<Node<Handler>> pipeline) {
            this.pipeline = pipeline;
            return this;
        }

        public Builder createDefaultPipeline() {
            this.pipeline = AgileContextUtils.createDefaultPipeline();
            return this;
        }

        public Builder getDefaultPipeline() {
            this.pipeline = AgileContextUtils.getDefaultPipeline();
            return this;
        }

        public Builder addHeadNode(Node<Handler> head) {
            checkPipeline();
            this.pipeline.addHead(head);
            return this;
        }

        public Builder addTailNode(Node<Handler> tail) {
            checkPipeline();
            this.pipeline.addTail(tail);
            return this;
        }

        public Builder addLastNode(Node<Handler> node) {
            checkPipeline();
            this.pipeline.addLast(node);
            return this;
        }

        public Builder addFirstNode(Node<Handler> node) {
            checkPipeline();
            this.pipeline.addFirst(node);
            return this;
        }

        public Builder insertNode(int index, Node<Handler> node) {
            checkPipeline();
            this.pipeline.insert(index, node);
            return this;
        }

        public Builder replaceNode(int index, Node<Handler> node) {
            checkPipeline();
            this.pipeline.replace(index, node);
            return this;
        }

        public Builder async(boolean async) {
            AgileContext.async = async;
            return this;
        }

        public Builder asyncExecutePool(int corePoolSize, int maximumPoolSize, int keepAliveTime, int awaitTimeout) {
            asyncCorePoolSize(corePoolSize).asyncMaximumPoolSize(maximumPoolSize).asyncKeepAliveTime(keepAliveTime).asyncAwaitTimeout(awaitTimeout);
            AgileContext.asyncExecutorService = new ThreadPoolExecutor(AgileContext.asyncCorePoolSize, AgileContext.asyncMaximumPoolSize,
                    AgileContext.asyncKeepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), AgileContext.asyncNamedThreadFactory);
            return this;
        }

        public Builder asyncExecutorService(ExecutorService executorService) {
            AgileContext.asyncExecutorService = executorService;
            return this;
        }

        public Builder asyncCorePoolSize(int corePoolSize) {
            if (corePoolSize < 0) {
                throw new InvalidDataException("CorePoolSize cannot be smaller than 0");
            }
            AgileContext.asyncCorePoolSize = corePoolSize;
            return this;
        }

        public Builder asyncMaximumPoolSize(int maximumPoolSize) {
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            if (maximumPoolSize > availableProcessors) {
                maximumPoolSize = availableProcessors;
            }
            AgileContext.asyncMaximumPoolSize = maximumPoolSize;
            return this;
        }

        public Builder asyncKeepAliveTime(int keepAliveTime) {
            if (keepAliveTime < 0) {
                throw new InvalidDataException("KeepAliveTime cannot be smaller than 0");
            }
            AgileContext.asyncKeepAliveTime = keepAliveTime;
            return this;
        }

        public Builder asyncAwaitTimeout(int awaitTimeout) {
            if (awaitTimeout < 0) {
                throw new InvalidDataException("AwaitTimeout cannot be smaller than 0");
            }
            AgileContext.asyncAwaitTimeout = awaitTimeout;
            return this;
        }

        public Builder redisHost(String redisHost) {
            AgileContext.redisHost = redisHost;
            return this;
        }

        public Builder redisPort(int redisPort) {
            AgileContext.redisPort = redisPort;
            return this;
        }

        public Builder redisDB(int redisDB) {
            if (redisDB < 0 || redisDB > 16) {
                throw new InvalidDataException("Only redis database between 0～16 can be set: %d", redisDB);
            }
            AgileContext.redisDB = redisDB;
            return this;
        }

        public Builder redisKeyPrefix(String redisKeyPrefix) {
            if (!redisKeyPrefix.endsWith("_")) {
                redisKeyPrefix += "_";
            }
            AgileContext.redisKeyPrefix = redisKeyPrefix;
            return this;
        }

        public Builder redisExpireSeconds(int redisExpireSeconds) {
            if (redisExpireSeconds < 0) {
                redisExpireSeconds = -1;
            }
            AgileContext.redisExpireSeconds = redisExpireSeconds;
            return this;
        }

        public void build() {
            if (!AgileContext.enable) {
                LoggerPrinter.info(log, "Agile Logger disabled");
                return;
            }
            if (this.pipeline == null) {
                this.createDefaultPipeline();
            }
            if (AgileContext.asyncExecutorService == null) {
                AgileContext.asyncExecutorService = new ThreadPoolExecutor(AgileContext.asyncCorePoolSize, AgileContext.asyncMaximumPoolSize,
                        AgileContext.asyncKeepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), AgileContext.asyncNamedThreadFactory);
            }
            AgileContext.pipeline = this.pipeline;

            // Call plugins SPI
            ServiceLoader<AgileLoggerPlugin> plugins = ServiceLoader.load(AgileLoggerPlugin.class);
            for (AgileLoggerPlugin plugin : plugins) {
                if (plugin.service(AgileContext.logMode)) {
                    break;
                }
            }

            LoggerPrinter.info(log, "Agile Logger is running in {}, Log mode: {}", AgileContext.async ? "async" : "sync", AgileContext.logMode.getName());
            LoggerPrinter.info(log, "Pipeline desc: {}", AgileContext.pipeline);
        }

        private void checkPipeline() {
            if (this.pipeline == null) {
                throw new PipelineNotDefinedException();
            }
            if (this.pipeline.size() < 2) {
                throw new PipelineDoesNotHaveEnoughNodesException();
            }
        }
    }

}
