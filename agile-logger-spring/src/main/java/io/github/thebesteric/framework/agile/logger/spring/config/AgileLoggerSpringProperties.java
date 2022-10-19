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

    // Async mode
    private Async async = new Async();

    // Support Maven and Gradle build tools
    private List<String> compilePaths = ClassPathUtils.compilePaths;

    // Url filter
    private UrlFilter urlFilter = new UrlFilter();

    // Response success define
    private ResponseSuccessDefine responseSuccessDefine;

    // Plugin
    private Plugins plugins = new Plugins();

    // RPC
    private Rpc rpc = new Rpc();

    // Config for others
    private Config config = new Config();

    @Getter
    @Setter
    public static class Async {
        // Decide whether to use a thread pool
        private boolean enable = true;
        // Thread pool parameters
        private AsyncParams asyncParams = new AsyncParams();
    }

    @Getter
    @Setter
    public static class Config {
        private Version version = new Version();
        private Mock mock = new Mock();
        private Track track = new Track();
        private Curl curl = new Curl();
    }

    @Getter
    @Setter
    public static class Curl {
        private boolean enable = true;
    }

    @Getter
    @Setter
    public static class Version {
        private String versionName;
    }

    @Getter
    @Setter
    public static class Track {
        // Set the name of the custom trackId
        private String trackIdName;
        // Decide whether to use SkyWalking trace id
        private boolean useSkyWalkingTrace = false;
    }

    @Getter
    @Setter
    public static class Mock {
        private boolean enable = true;
        private int expireAfterWrite = 600;
        private int expireAfterAccess = 600;

        @Override
        public String toString() {
            return "[" +
                    "enable=" + enable + ", " +
                    "expireAfterWrite=" + expireAfterWrite + "s, " +
                    "expireAfterAccess=" + expireAfterAccess + "s" +
                    "]";
        }
    }

    @Getter
    @Setter
    public static class Rpc {

        private Feign feign = new Feign();

        @Getter
        @Setter
        public static class Feign {
            private boolean enable = true;
        }
    }

    @Getter
    @Setter
    public static class Plugins {
        // Redis-Plugin
        private Redis redis = new Redis();

        // Database-Plugin
        private Database database = new Database();

        // Cache-Plugin
        private Cache cache = new Cache();
    }

    @Getter
    @Setter
    public static class Cache {
        private int initialCapacity = 2000;
        private int maximumSize = 20000;
        private int expiredTime = 1000 * 60 * 60;
        ;

        @Override
        public String toString() {
            return "[" +
                    "initialCapacity=" + initialCapacity + ", " +
                    "maximumSize=" + maximumSize + ", " +
                    "expiredTime=" + MathUtils.divStripTrailingZeros((double) expiredTime, 1000D) + "s" +
                    "]";
        }
    }

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
        private String keyPrefix = "agile";
        private String host = "localhost";
        private int port = 6379;
        private int database = 1;
        private int expiredTime = 1000 * 60 * 60;
        private String username;
        private String password;
        private int timeout = 10000;
        private int shutdownTimeout = 10000;
        private int minIdle = 1;
        private int maxIdle = 8;
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
        private int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        private int keepAliveTime = 60 * 1000;
        private int queueSize = 1024;
        private String threadNamePrefix = AgileLoggerConstant.THREAD_POOL_NAME;

        @Override
        public String toString() {
            return "[" +
                    "corePoolSize=" + corePoolSize + ", " +
                    "maximumPoolSize=" + maximumPoolSize + ", " +
                    "queueSize=" + queueSize + ", " +
                    "keepAliveTime=" + MathUtils.divStripTrailingZeros((double) keepAliveTime, 1000D) + "s" +
                    "]";
        }
    }

    @Getter
    @Setter
    public static class UrlFilter {
        // Matches valid urls
        private String[] includes = {".*"};
        // Matches invalid urls
        private String[] excludes = {};
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
