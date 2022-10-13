package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.processor;

import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.MockCache;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.annotation.Mocker;

import java.lang.reflect.Method;

/**
 * ValueMockProcessor
 * <p>Process @Mocker's value attribute
 *
 * @author Eric Joe
 * @version 1.0
 */
public class ValueMockProcessor extends AbstractCachedMockProcessor {

    public ValueMockProcessor(MockCache mockCache) {
        super(mockCache);
    }

    @Override
    public boolean match(Mocker mocker) {
        return !StringUtils.isEmpty(mocker.value());
    }

    @Override
    public Object doProcess(Mocker mocker, Method method, Object[] args) throws Throwable {
        String mockStr = mocker.value();
        Class<?> returnType = method.getReturnType();
        if (StringUtils.isNotEmpty(mockStr)) {
            return handleMockValue(mockStr, returnType);
        }
        return null;
    }

    @Override
    public int order() {
        return MockOrderEnum.VALUE_ORDER.order();
    }
}
