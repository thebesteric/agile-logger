package io.github.thebesteric.framework.agile.logger.plugin.redis.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.thebesteric.framework.agile.logger.commons.utils.JsonUtils;
import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.JoinMethod;
import io.github.thebesteric.framework.agile.logger.core.handler.AbstractTailHandler;
import io.github.thebesteric.framework.agile.logger.core.handler.InternalTailHandler;
import io.github.thebesteric.framework.agile.logger.plugin.redis.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

/**
 * InternalRedisTailHandler
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-10 13:38:22
 */
public class InternalRedisTailHandler extends AbstractTailHandler {

    private final RedisUtils redisUtils;

    public InternalRedisTailHandler(JedisPool pool) {
        this.redisUtils = new RedisUtils(pool, AgileContext.redisDB);
    }

    private static final Logger log = LoggerFactory.getLogger(InternalTailHandler.class);

    @Override
    public void process(AgileContext ctx, JoinMethod joinMethod, InvokeLog invokeLog) {
        try {
            String key = AgileContext.redisKeyPrefix + invokeLog.getTrackId() + ":" + invokeLog.getId();
            redisUtils.set(key, JsonUtils.mapper.writeValueAsString(invokeLog), AgileContext.redisExpireSeconds);
        } catch (JsonProcessingException e) {
            log.error("Can't serialize this object: {}", invokeLog.getClass().getName());
            e.printStackTrace();
        }
    }
}
