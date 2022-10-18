package io.github.thebesteric.framework.agile.logger.spring.plugin.versioner;

import io.github.thebesteric.framework.agile.logger.commons.utils.CollectionUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.ReflectUtils;
import io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.annotation.Versioner;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.List;

/**
 * VersionInfo
 *
 * @author Eric Joe
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public class VersionerInfo {
    private final VersionerAdapter instance;
    private final Object[] args;

    public VersionerInfo(Method method, Object[] args) throws Exception {
        Versioner versioner = method.getAnnotation(Versioner.class);
        Class<? extends VersionerAdapter> versionAdapter = versioner.type();
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
        Class<? extends VersionerAdapter> versionAdapterClass = this.instance.getClass();
        if (versionAdapterClass.getSuperclass() == ResponseVersionerAdapter.class) {
            // It's ResponseVersionerAdapter<R> must skip it
            return null;
        }
        Class<?> requestType = ReflectUtils.getActualTypeArguments(versionAdapterClass, VersionerAdapter.class).get(0);
        for (Object arg : this.args) {
            if (requestType.isAssignableFrom(arg.getClass())) {
                try {
                    Method requestMethod = versionAdapterClass.getMethod(Versioner.REQUEST_METHOD_NAME, requestType);
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
        Class<? extends VersionerAdapter> versionAdapterClass = this.instance.getClass();
        if (versionAdapterClass.getSuperclass() == RequestVersionerAdapter.class) {
            // It's RequestVersionerAdapter<V> must skip it
            return null;
        }
        List<Class<?>> actualTypeArguments = ReflectUtils.getActualTypeArguments(versionAdapterClass, VersionerAdapter.class);
        if (CollectionUtils.isNotEmpty(actualTypeArguments)) {
            // When actualTypeArguments == 0 is ResponseVersionerAdapter<R>
            Class<?> responseType = actualTypeArguments.size() > 1 ? actualTypeArguments.get(1) : actualTypeArguments.get(0);
            try {
                Method responseMethod = versionAdapterClass.getMethod(Versioner.RESPONSE_METHOD_NAME, responseType);
                return new MethodInfo(instance, responseMethod, result);
            } catch (NoSuchMethodException ignored) {
                // The response method is not overridden
                return null;
            }
        }
        return null;
    }

    @Getter
    @Setter
    public static class MethodInfo {
        private VersionerAdapter instance;
        private Method method;
        private Object arg;

        public MethodInfo(VersionerAdapter instance, Method method, Object arg) {
            this.instance = instance;
            this.method = method;
            this.arg = arg;
        }

        public Object invoke() throws Exception {
            return this.method.invoke(this.instance, this.arg);
        }
    }
}
