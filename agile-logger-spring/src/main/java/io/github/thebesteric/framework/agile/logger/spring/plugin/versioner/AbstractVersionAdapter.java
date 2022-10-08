package io.github.thebesteric.framework.agile.logger.spring.plugin.versioner;

import lombok.Getter;

/**
 * AbstractVersionAdapter
 *
 * @author Eric Joe
 * @version 1.0
 */
public abstract class AbstractVersionAdapter<V, R> implements VersionAdapter<V, R> {

    @Getter
    private Object[] args;

    @Override
    public void args(Object[] args) {
        this.args = args;
    }

    @Override
    public void request(V v) {
    }

    @Override
    public Object response(R result) {
        return result;
    }
}
