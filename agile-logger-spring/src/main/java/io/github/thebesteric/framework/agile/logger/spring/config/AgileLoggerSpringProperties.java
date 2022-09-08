package io.github.thebesteric.framework.agile.logger.spring.config;

import io.github.thebesteric.framework.agile.logger.commons.AgileLoggerConstant;
import io.github.thebesteric.framework.agile.logger.commons.utils.ClassPathUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.MathUtils;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * AgileLoggerProperties
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-16 11:29:30
 */
@Getter
@Setter
@ConfigurationProperties(prefix = AgileLoggerConstant.PROPERTIES_PREFIX)
public class AgileLoggerSpringProperties {

    private boolean enable = true;

    // LOG, STDOUT, CACHE, REDIS, ES, DATABASE
    private LogMode logMode = LogMode.STDOUT;

    // Decide whether to use a thread pool
    private boolean async = true;

    // Thread pool parameters
    private AsyncParams asyncParams = new AsyncParams();

    // Modifiers on class or method that need to be ignored
    private IgnoreModifiers ignoreModifiers = new IgnoreModifiers();

    // Support Maven and Gradle build tools
    private List<String> compilePaths = ClassPathUtils.compilePaths;

    // Decide whether to use SkyWalking trace id
    private boolean useSkyWalkingTrace = false;

    // Url filter
    private UrlFilter urlFilter = new UrlFilter();

    // Response success define
    private ResponseSuccessDefine responseSuccessDefine;

    // Redis-Plugin
    private Redis redis = new Redis();

    // Database-Plugin
    private Database database = new Database();

    @Getter
    @Setter
    public static class Database {
        private String tableNamePrefix = "agile_logger";
        private String url;
        private String driverClassName;
        private String username;
        private String password;
        private int minIdle = 1;
        private int maxActive = 8;

        @Override
        public String toString() {
            return "[" +
                    "url=" + url + ", " +
                    "driverClass=" + driverClassName +
                    "]";
        }
    }

    @Getter
    @Setter
    public static class Redis {
        private String keyPrefix = "agile_";
        private String host = "localhost";
        private int port = 6379;
        private int database = 1;
        private int expiredTime = 1000 * 60 * 60;
        private String username;
        private String password;
        private int timeout = 10000;
        private int shutdownTimeout = 10000;
        private int maxIdle = 8;
        private int minIdle = 1;
        private int maxActive = 8;
        private int maxWait = -1;

        @Override
        public String toString() {
            return "[" +
                    "host=" + host + ", " +
                    "port=" + port + ", " +
                    "database=" + database + ", " +
                    "expiredTime=" + MathUtils.divStripTrailingZeros((double) expiredTime, 1000D) + "s" +
                    "]";
        }
    }

    @Getter
    @Setter
    public static class AsyncParams {
        private int corePoolSize = 1;
        private int maximumPoolSize = 1;
        private int keepAliveTime = 0;
        private int queueSize = 1024;
        private String threadNamePrefix = AgileLoggerConstant.THREAD_POOL_NAME;

        @Override
        public String toString() {
            return "[" +
                    "corePoolSize=" + corePoolSize + ", " +
                    "maximumPoolSize=" + maximumPoolSize + ", " +
                    "keepAliveTime=" + keepAliveTime + ", " +
                    "queueSize=" + queueSize +
                    "]";
        }
    }

    @Getter
    @Setter
    public static class UrlFilter {
        // Matches whether the URL can enter the filter
        private String[] urlPatterns = {"/*"};
        // Matches valid urls
        private String[] includes = {".*"};
        // Matches invalid urls
        private String[] excludes = {};
    }

    @Getter
    @Setter
    public static class IgnoreModifiers {
        private IgnoreModifier type = new IgnoreModifier();
        private IgnoreModifier method = new IgnoreModifier();

        @Getter
        @Setter
        public static class IgnoreModifier {
            private boolean privateModifier = false;
            private boolean staticModifier = false;
        }
    }

    @Getter
    @Setter
    public static class ResponseSuccessDefine {
        private List<CodeField> codeFields;
        private List<String> messageFields;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class CodeField {
            private String name;
            private Object value;
        }
    }
}
