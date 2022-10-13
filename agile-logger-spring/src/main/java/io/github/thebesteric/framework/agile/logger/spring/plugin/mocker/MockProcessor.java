package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker;

import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.annotation.Mocker;

import java.lang.reflect.Method;

/**
 * MockProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
public interface MockProcessor {

    boolean match(Mocker mocker);

    Object process(Mocker mocker, Method method, Object[] args) throws Throwable;

    int order();
}
