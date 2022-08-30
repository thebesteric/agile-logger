package io.github.thebesteric.framework.agile.logger.core.domain;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * JoinMethod
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-15 19:12:55
 */
public class JoinMethod {
    private final ProceedingJoinPoint joinPoint;

    private final Class<?> declaringType;

    private final Method method;

    private final Class<?>[] methodParameterTypes;

    public JoinMethod(JoinPoint joinPoint) {
        try {
            this.joinPoint = (ProceedingJoinPoint) joinPoint;
            this.declaringType = joinPoint.getSignature().getDeclaringType();
            this.methodParameterTypes = ((CodeSignature) joinPoint.getSignature()).getParameterTypes();
            this.method = this.declaringType.getDeclaredMethod(joinPoint.getSignature().getName(), this.methodParameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Annotation> T getAnnotationOnMethod(Class<T> annotationClass) {
        return this.method.getAnnotation(annotationClass);
    }

    public <T extends Annotation> T getAnnotationOnType(Class<T> annotationClass) {
        return this.declaringType.getAnnotation(annotationClass);
    }

    public <T extends Annotation> boolean isAnnotationOnMethod(Class<T> annotationClass) {
        return this.method.isAnnotationPresent(annotationClass);
    }

    public <T extends Annotation> boolean isAnnotationOnType(Class<T> annotationClass) {
        return this.declaringType.isAnnotationPresent(annotationClass);
    }


    /* getter */

    public JoinPoint getJoinPoint() {
        return joinPoint;
    }

    public Class<?> getDeclaringType() {
        return declaringType;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?>[] getMethodParameterTypes() {
        return methodParameterTypes;
    }
}
