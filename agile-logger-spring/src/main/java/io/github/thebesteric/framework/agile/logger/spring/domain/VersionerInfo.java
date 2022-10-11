package io.github.thebesteric.framework.agile.logger.spring.domain;

import io.github.thebesteric.framework.agile.logger.commons.utils.ReflectUtils;
import io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.VersionAdapter;
import io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.annotation.Versioner;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * VersionInfo
 *
 * @author Eric Joe
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public class VersionerInfo {
    private final VersionAdapter instance;
    private final Object[] args;

    public VersionerInfo(Method method, Object[] args) throws Exception {
        Versioner versioner = method.getAnnotation(Versioner.class);
        Class<? extends VersionAdapter> versionAdapter = versioner.type();
        this.instance = versionAdapter.getDeclaredConstructor().newInstance();
        this.args = args;

        // Call method & args method
        this.instance.method(method);
        this.instance.args(args);
    }

    /**
     * Get Versioner Request Method
     *
     * @return {@link MethodInfo}
     */
    public MethodInfo getRequestMethodInfo() throws Exception {
        Class<?> requestType = ReflectUtils.getActualTypeArguments(this.instance.getClass(), VersionAdapter.class).get(0);
        for (Object arg : this.args) {
            if (arg.getClass().isAssignableFrom(requestType)) {
                try {
                    Method requestMethod = instance.getClass().getMethod(Versioner.REQUEST_METHOD_NAME, arg.getClass());
                    return new MethodInfo(instance, requestMethod, arg);
                } catch (NoSuchMethodException ignored) {
                    // The request method is not overridden
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Get Versioner Response Method
     *
     * @param result result
     * @return {@link MethodInfo}
     */
    public MethodInfo getResponseMethodInfo(Object result) throws Exception {
        Class<?> responseType = ReflectUtils.getActualTypeArguments(this.instance.getClass(), VersionAdapter.class).get(1);
        try {
            Method responseMethod = instance.getClass().getMethod(Versioner.RESPONSE_METHOD_NAME, responseType);
            return new MethodInfo(instance, responseMethod, result);
        } catch (NoSuchMethodException ignored) {
            // The response method is not overridden
            return null;
        }
    }

    @Getter
    @Setter
    public static class MethodInfo {
        private VersionAdapter instance;
        private Method method;
        private Object arg;

        public MethodInfo(VersionAdapter instance, Method method, Object arg) {
            this.instance = instance;
            this.method = method;
            this.arg = arg;
        }

        public Object invoke() throws Exception {
            return this.method.invoke(this.instance, this.arg);
        }
    }
}
