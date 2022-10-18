package io.github.thebesteric.framework.agile.logger.spring.plugin.versioner;

/**
 * RequestVersionerAdapter
 *
 * @author Eric Joe
 * @version 1.0
 */
public abstract class ResponseVersionerAdapter<R> extends AbstractVersionerAdapter<Object, R> {
    @Override
    public abstract R response(R result);

    @Override
    public final void request(Object o) {
        super.request(o);
    }
}
