package io.github.thebesteric.framework.agile.logger.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.thebesteric.framework.agile.logger.core.domain.AgileLoggerConstant;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import io.github.thebesteric.framework.agile.logger.core.handler.Handler;
import io.github.thebesteric.framework.agile.logger.core.pipeline.Node;
import io.github.thebesteric.framework.agile.logger.core.pipeline.Pipeline;
import io.github.thebesteric.framework.agile.logger.core.utils.IdGenerator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * AgileLoogerProperties
 *
 * @author Eric Joe
 * @since 1.0
 */
public class AgileLoggerProperties {

    /** The global switch. Whether to open the aspect */
    public static boolean enable = false;
    public static LogMode logMode = LogMode.STDOUT;

    public static Pipeline<Node<Handler>> pipeline;

    public static IdGenerator idGenerator;
    public static IdGenerator trackIdGenerator;

    // For Async mode
    public static boolean async = true;
    public static int asyncCorePoolSize = 1;
    public static int asyncMaximumPoolSize = 1;
    public static int asyncKeepAliveTime = 60;
    public static int asyncAwaitTimeout = 5;
    public static ExecutorService asyncExecutorService;
    public static ThreadFactory asyncNamedThreadFactory = new ThreadFactoryBuilder().setNameFormat(AgileLoggerConstant.THREAD_POOL_NAME + "-%d").build();

    // For Redis mode
    public static String redisHost = "localhost";
    public static int redisPort = 6379;
    public static int redisDB = 0;
    public static String redisKeyPrefix = "agile_";
    public static int redisExpireSeconds = -1;

}
