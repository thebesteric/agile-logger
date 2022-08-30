package io.github.thebesteric.framework.agile.logger.spring.config;

import io.github.thebesteric.framework.agile.logger.commons.utils.ClassPathUtils;
import io.github.thebesteric.framework.agile.logger.core.domain.AgileLoggerConstant;
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
@ConfigurationProperties(prefix = AgileLoggerSpringProperties.PROPERTIES_PREFIX)
public class AgileLoggerSpringProperties {
    public static final String PROPERTIES_PREFIX = "sourceflag.agile-logger";

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
