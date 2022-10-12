package io.github.thebesteric.framework.agile.logger.spring.plugin.versioner;

import java.lang.reflect.Method;

/**
 * VersionerAdapter
 *
 * @author Eric Joe
 * @since 1.0
 */
public interface VersionerAdapter<V, R> {
    void args(Object[] args);
    void method(Method method);
    void request(V v);
    R response(R result);
}
