package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker;

import io.github.thebesteric.framework.agile.logger.commons.domain.MethodInfo;
import io.github.thebesteric.framework.agile.logger.commons.exception.IllegalDataTypeException;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

/**
 * MethodsMockerAdapter
 *
 * @author Eric Joe
 * @version 1.0
 */
public abstract class MethodsMockerAdapter extends AbstractMockerAdapter<Object> {

    @Setter
    @Getter
    private Object instance;

    @Override
    public Object mock() {
        return determineAndInvokeMockMethod(methodInfo);
    }

    @SneakyThrows
    private Object determineAndInvokeMockMethod(MethodInfo methodInfo) {
        Method mockMethod;
        try {
            mockMethod = instance.getClass().getMethod(methodInfo.getName());
        } catch (Exception e) {
            // There is no parameterless mock method with the same name as the execution method definition
            return null;
        }
        if (mockMethod.getReturnType() != methodInfo.getReturnType()) {
            throw new IllegalDataTypeException("%s can not cast to %s", mockMethod.getReturnType().getName(), methodInfo.getReturnType().getName());
        }
        return mockMethod.invoke(instance);
    }
}
