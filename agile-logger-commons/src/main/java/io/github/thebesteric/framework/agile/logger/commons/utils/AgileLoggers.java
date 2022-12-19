package io.github.thebesteric.framework.agile.logger.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
public class AgileLoggers {

    public static final Stdout STDOUT;
    public static final Logger LOG;

    static {
        STDOUT = new Stdout();
        LOG = new Logger();
    }

    @Slf4j
    public static class Logger implements LogLevel {

        public void trace(String message, Object... args) {
            log.trace(new Log(getActualExecutor(), StringUtils.format(message, args)).toString());
        }

        public void debug(String message, Object... args) {
            log.debug(new Log(getActualExecutor(), StringUtils.format(message, args)).toString());
        }

        @Override
        public void info(String message, Object... args) {
            log.info(new Log(getActualExecutor(), StringUtils.format(message, args)).toString());
        }

        @Override
        public void warn(String message, Object... args) {
            log.warn(new Log(getActualExecutor(), StringUtils.format(message, args)).toString());
        }

        @Override
        public void error(String message, Object... args) {
            log.error(new Log(getActualExecutor(), StringUtils.format(message, args)).toString());
        }

    }

    public static class Stdout implements LogLevel {

        @Override
        public void info(String message, Object... args) {
            System.out.println(new Log(getActualExecutor(), StringUtils.format(message, args)));
        }

        @Override
        public void warn(String message, Object... args) {
            error(message, args);
        }

        @Override
        public void error(String message, Object... args) {
            System.err.println(new Log(getActualExecutor(), StringUtils.format(message, args)));
        }

    }

    private interface LogLevel {
        void info(String message, Object... args);

        void warn(String message, Object... args);

        void error(String message, Object... args);
    }

    private static class ActualExecutor {
        private final Class<?> clazz;
        private final Method method;
        private final int lineNumber;

        public ActualExecutor(final String className, final String methodName, final int lineNumber) {
            try {
                this.clazz = Class.forName(className);
                this.method = Arrays.stream(this.clazz.getDeclaredMethods()).filter((method -> methodName.equals(method.getName()))).findFirst().orElse(null);
                this.lineNumber = lineNumber;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static ActualExecutor getActualExecutor() {
        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
        StackTraceElement stackTraceElement = stackTrace[1];
        if (stackTrace.length >= 3) {
            stackTraceElement = stackTrace[2];
        }
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();
        int lineNumber = stackTraceElement.getLineNumber();
        return new ActualExecutor(className, methodName, lineNumber);
    }

    private static class Log {
        private String trackId;
        private String className;
        private String methodName;
        private Class<?>[] methodArgs;
        private int lineNumber;
        private String message;

        public Log(ActualExecutor actualExecutor, String message) {
            this.trackId = TransactionUtils.get();
            this.className = actualExecutor.clazz.getName();
            this.methodName = actualExecutor.method.getName();
            this.methodArgs = actualExecutor.method.getParameterTypes();
            this.lineNumber = actualExecutor.lineNumber;
            this.message = message;
        }

        public String getTrackId() {
            return trackId;
        }

        public void setTrackId(String trackId) {
            this.trackId = trackId;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public Class<?>[] getMethodArgs() {
            return methodArgs;
        }

        public void setMethodArgs(Class<?>[] methodArgs) {
            this.methodArgs = methodArgs;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            try {
                return JsonUtils.mapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
