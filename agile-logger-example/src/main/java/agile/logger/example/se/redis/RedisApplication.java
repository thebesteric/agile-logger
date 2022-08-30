package agile.logger.example.se.redis;

import agile.logger.example.domain.MathCalc;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLoggerEntrance;
import io.github.thebesteric.framework.agile.logger.core.utils.AgileLoggerHelper;

/**
 * Demo Entrance
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-25
 */
@AgileLogger(tag = "application")
public class RedisApplication {

    static {
        AgileLoggerHelper.builder()
                .createDefaultPipeline()
                .redisDB(1)
                .redisHost("127.0.0.1")
                .redisPort(6379)
                .redisKeyPrefix("agile_logger")
                .redisExpireSeconds(5 * 60)
                .async(true)
                .asyncExecutePool(1, 2, 10, 1)
                .build();
    }

    @AgileLoggerEntrance
    public static void main(String[] args) throws InterruptedException {
        MathCalc calc = new MathCalc();
        calc.add(1, 2);
        calc.minus(1, 2);
    }
}
