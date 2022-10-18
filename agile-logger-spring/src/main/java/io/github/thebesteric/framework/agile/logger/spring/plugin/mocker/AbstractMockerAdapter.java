package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker;

import io.github.thebesteric.framework.agile.logger.commons.domain.MethodInfo;
import lombok.Getter;

/**
 * AbstractMockerAdapter
 *
 * @author Eric Joe
 * @version 1.0
 */
public abstract class AbstractMockerAdapter<R> implements MockerAdapter<R> {
    @Getter
    protected Object[] args;

    @Getter
    protected MethodInfo methodInfo;

    @Override
    public void args(Object[] args) {
        this.args = args;
    }

    @Override
    public void methodInfo(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }
}
