package io.github.thebesteric.framework.agile.logger.spring.plugin.versioner;

/**
 * RequestVersionerAdapter
 *
 * @author Eric Joe
 * @version 1.0
 */
public abstract class RequestVersionerAdapter<V> extends AbstractVersionerAdapter<V, Object> {

    @Override
    public abstract void request(V v);

    @Override
    public final Object response(Object result) {
        return super.response(result);
    }
}
