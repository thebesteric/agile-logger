package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker;

import io.github.thebesteric.framework.agile.logger.commons.domain.MethodInfo;

/**
 * MockerAdapter
 *
 * @author Eric Joe
 * @version 1.0
 */
public interface MockerAdapter<R> {
    R mock();

    void args(Object[] args);

    void methodInfo(MethodInfo methodInfo);
}
