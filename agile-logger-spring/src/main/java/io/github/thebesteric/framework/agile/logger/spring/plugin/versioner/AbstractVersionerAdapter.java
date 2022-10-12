package io.github.thebesteric.framework.agile.logger.spring.plugin.versioner;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * AbstractVersionerAdapter
 *
 * @author Eric Joe
 * @version 1.0
 */
public abstract class AbstractVersionerAdapter<V, R> implements VersionerAdapter<V, R> {

    @Getter
    private Object[] args;

    @Getter
    private Method method;

    @Override
    public void args(Object[] args) {
        this.args = args;
    }

    @Override
    public void method(Method method) {
        this.method = method;
    }

    @Override
    public void request(V v) {
    }

    @Override
    public R response(R result) {
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> T getArg(Class<T> clazz) {
        return (T) Arrays.stream(this.args).filter(arg-> arg.getClass() == clazz).findFirst().orElse(null);
    }
}
