package io.github.thebesteric.framework.agile.logger.plugin.redis.spring.record;

import io.github.thebesteric.framework.agile.logger.commons.exception.UnsupportedModeException;
import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import io.github.thebesteric.framework.agile.logger.spring.processor.record.AbstractThreadPoolRecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

/**
 * RedisRecordProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
public class RedisRecordProcessor extends AbstractThreadPoolRecordProcessor {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisRecordProcessor(AgileLoggerContext agileLoggerContext, RedisTemplate<String, Object> redisTemplate) {
        super(agileLoggerContext);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean supports(LogMode model) throws UnsupportedModeException {
        return redisTemplate != null && model != null && !model.getName().trim().equals("")
                && LogMode.REDIS.getName().equalsIgnoreCase(model.getName());
    }

    @Override
    public void doProcess(InvokeLog invokeLog) throws Throwable {
        String key = AgileContext.redisKeyPrefix + invokeLog.getTrackId() + ":" + invokeLog.getLogId();
        int expiredTime = this.agileLoggerContext.getProperties().getRedis().getExpiredTime();
        redisTemplate.opsForValue().set(key, invokeLog, Duration.ofMillis(expiredTime));
    }
}
