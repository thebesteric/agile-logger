package io.github.thebesteric.framework.agile.logger.spring.plugin.versioner;

/**
 * VersionAdapter
 *
 * @author Eric Joe
 * @since 1.0
 */
public interface VersionAdapter<V, R> {
    void args(Object[] args);
    void request(V v);
    R response(R result);
}
