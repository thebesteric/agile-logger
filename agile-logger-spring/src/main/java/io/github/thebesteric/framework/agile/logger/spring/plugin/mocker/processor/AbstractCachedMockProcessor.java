package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.processor;

import io.github.thebesteric.framework.agile.logger.commons.exception.ParseErrorException;
import io.github.thebesteric.framework.agile.logger.commons.utils.JsonUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.ReflectUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.SignatureUtils;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.MockCache;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.MockProcessor;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.annotation.Mocker;

import java.lang.reflect.Method;

/**
 * AbstractMockProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
public abstract class AbstractCachedMockProcessor implements MockProcessor {

    protected final MockCache mockCache;

    public AbstractCachedMockProcessor(MockCache mockCache) {
        this.mockCache = mockCache;
    }

    @Override
    public Object process(Mocker mocker, Method method, Object[] args) throws Throwable {
        String key = SignatureUtils.methodSignature(method);
        Object mockInstance;
        if (mocker.cache()) {
            mockInstance = mockCache.get(key);
            if (mockInstance != null) {
                return mockInstance;
            }
        }
        mockInstance = this.doProcess(mocker, method, args);
        if (mockInstance != null && mocker.cache()) {
            mockCache.put(key, mockInstance);
        }
        return mockInstance;
    }

    public Object handleMockValue(String mockValue, Class<?> returnType) {
        try {
            if (returnType == Void.class) {
                return null;
            } else if (ReflectUtils.isPrimitiveOrWarp(returnType) || returnType == String.class) {
                return ReflectUtils.parsePrimitiveOrWarpByType(mockValue, returnType);
            }
            return JsonUtils.mapper.readValue(JsonUtils.formatJson(mockValue), returnType);
        } catch (Throwable throwable) {
            throw new ParseErrorException("Cannot parse %s to %s: %s", mockValue, returnType.getName(), throwable.getMessage());
        }
    }

    public abstract Object doProcess(Mocker mocker, Method method, Object[] args) throws Throwable;
}
