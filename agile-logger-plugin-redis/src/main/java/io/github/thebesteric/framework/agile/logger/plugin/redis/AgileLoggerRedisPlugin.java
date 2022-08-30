package io.github.thebesteric.framework.agile.logger.plugin.redis;

import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import io.github.thebesteric.framework.agile.logger.core.handler.Handler;
import io.github.thebesteric.framework.agile.logger.core.pipeline.Node;
import io.github.thebesteric.framework.agile.logger.core.plugin.AgileLoggerPlugin;
import io.github.thebesteric.framework.agile.logger.plugin.redis.handler.InternalRedisTailHandler;
import redis.clients.jedis.JedisPool;

/**
 * <p>Service Provider Interface
 * <p>For JavaSE Project plugin
 *
 * @author Eric Joe
 * @since 1.0
 */
public class AgileLoggerRedisPlugin implements AgileLoggerPlugin {
    @Override
    public boolean service(LogMode logMode) {
        if (LogMode.REDIS.equals(logMode)) {
            JedisPool jedisPool = new JedisPool(AgileContext.redisHost, AgileContext.redisPort);
            Node<Handler> redisTailNode = new Node<>(new InternalRedisTailHandler(jedisPool));
            AgileContext.pipeline.replaceTail(redisTailNode);
            return true;
        }
        return false;
    }
}
