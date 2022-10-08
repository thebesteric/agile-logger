package io.github.thebesteric.framework.agile.logger.spring.domain;

import io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.VersionAdapter;
import io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.annotation.Versioner;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

    public VersionerInfo(Versioner versioner, Object[] args) throws Exception {
        assert versioner != null;
        Class<? extends VersionAdapter> versionAdapter = versioner.type();
        this.instance = versionAdapter.getDeclaredConstructor().newInstance();
        this.args = args;

        // Call args method
        this.instance.args(args);
    }

    public MethodInfo getRequestMethodInfo() throws NoSuchMethodException {
        ParameterizedType parameterizedType = (ParameterizedType) this.instance.getClass().getGenericSuperclass();
        Type requestType = parameterizedType.getActualTypeArguments()[0];

        for (Object arg : this.args) {
            if (arg.getClass().isAssignableFrom((Class<?>) requestType)) {
                Method requestMethod = instance.getClass().getMethod(Versioner.REQUEST_METHOD_NAME, arg.getClass());
                return new MethodInfo(instance, requestMethod, arg);
            }
        }
        return null;
    }

    public MethodInfo getResponseMethodInfo(Object result) throws NoSuchMethodException {

        ParameterizedType parameterizedType = (ParameterizedType) this.instance.getClass().getGenericSuperclass();
        Type responseType = parameterizedType.getActualTypeArguments()[1];

        Method responseMethod = instance.getClass().getMethod(Versioner.RESPONSE_METHOD_NAME, (Class<?>) responseType);
        return new MethodInfo(instance, responseMethod, result);
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
