package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.processor;

import io.github.thebesteric.framework.agile.logger.commons.domain.MethodInfo;
import io.github.thebesteric.framework.agile.logger.commons.exception.IllegalArgumentException;
import io.github.thebesteric.framework.agile.logger.commons.utils.ReflectUtils;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.MethodsMockerAdapter;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.MockCache;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.MockerAdapter;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.NoMocker;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.annotation.Mocker;

import java.lang.reflect.Method;

/**
 * TypeMockProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
public class TypeMockProcessor extends AbstractCachedMockProcessor {

    public TypeMockProcessor(MockCache mockCache) {
        super(mockCache);
    }

    @Override
    public boolean match(Mocker mocker) {
        return mocker.type() != NoMocker.class;
    }

    @Override
    public int order() {
        return MockOrderEnum.TYPE_ORDER.order();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object doProcess(Mocker mocker, Method method, Object[] args) throws Throwable {
        Class<? extends MockerAdapter> mockType = mocker.type();
        Class<?> actualType = ReflectUtils.getActualTypeArguments(mockType, MockerAdapter.class).get(0);
        Class<?> returnType = method.getReturnType();
        if (returnType.isAssignableFrom(actualType) || actualType == Object.class) {
            Method mockMethod = mockType.getMethod(Mocker.MOCK_METHOD_NAME);
            MockerAdapter instance = mockType.getDeclaredConstructor().newInstance();
            instance.methodInfo(new MethodInfo(method));
            instance.args(args);
            if (MethodsMockerAdapter.class.isAssignableFrom(mockType)) {
                MethodsMockerAdapter methodsMockerAdapterInstance = (MethodsMockerAdapter) instance;
                methodsMockerAdapterInstance.setInstance(methodsMockerAdapterInstance);
            }
            return mockMethod.invoke(instance);
        } else if (returnType == Void.class) {
            return null;
        }
        throw new IllegalArgumentException("Can not cast %s to %s", actualType.getName(), returnType.getName());
    }
}
