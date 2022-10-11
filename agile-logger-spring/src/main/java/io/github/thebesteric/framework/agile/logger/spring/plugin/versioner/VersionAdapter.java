package io.github.thebesteric.framework.agile.logger.spring.plugin.versioner;

import java.lang.reflect.Method;

/**
 * VersionAdapter
 *
 * @author Eric Joe
 * @since 1.0
 */
public interface VersionAdapter<V, R> {
    void args(Object[] args);
    void method(Method method);
    void request(V v);
    R response(R result);
}
