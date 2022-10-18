package io.github.thebesteric.framework.agile.logger.commons.domain;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * MethodInfo
 *
 * @author Eric Joe
 * @version 1.0
 */
public class MethodInfo {

    private final Method method;

    public MethodInfo(Method method) {
        this.method = method;
    }

    public String getName() {
        return this.method.getName();
    }

    public Class<?> getReturnType() {
        return this.method.getReturnType();
    }

    public Class<?>[] getParameterTypes() {
        return this.method.getParameterTypes();
    }

    public Class<?> getDeclaringClass() {
        return this.method.getDeclaringClass();
    }

    public int getModifiers() {
        return this.method.getModifiers();
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return this.method.getAnnotation(annotationClass);
    }

    public int getParameterCount() {
        return this.method.getParameterCount();
    }

    public AnnotatedType getAnnotatedReturnType() {
        return this.method.getAnnotatedReturnType();
    }

    public Annotation[] getDeclaredAnnotations() {
        return this.method.getDeclaredAnnotations();
    }

    public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        return this.method.getDeclaredAnnotation(annotationClass);
    }

    public Object getDefaultValue() {
        return this.method.getDefaultValue();
    }

    public Class<?>[] getExceptionTypes() {
        return this.method.getExceptionTypes();
    }

    public Type[] getGenericExceptionTypes() {
        return this.method.getGenericExceptionTypes();
    }

    public Type[] getGenericParameterTypes() {
        return this.method.getGenericParameterTypes();
    }

    public Type getGenericReturnType() {
        return this.method.getGenericReturnType();
    }

    public Annotation[][] getParameterAnnotations() {
        return this.method.getParameterAnnotations();
    }

    public TypeVariable<Method>[] getTypeParameters() {
        return this.method.getTypeParameters();
    }

    public AnnotatedType[] getAnnotatedExceptionTypes() {
        return this.method.getAnnotatedExceptionTypes();
    }

    public AnnotatedType[] getAnnotatedParameterTypes() {
        return this.method.getAnnotatedParameterTypes();
    }

    public AnnotatedType getAnnotatedReceiverType() {
        return this.method.getAnnotatedReceiverType();
    }

    public Annotation[] getAnnotations() {
        return this.method.getAnnotations();
    }

    public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
        return this.method.getAnnotationsByType(annotationClass);
    }

    public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
        return this.method.getDeclaredAnnotationsByType(annotationClass);
    }

    public Parameter[] getParameters() {
        return this.method.getParameters();
    }

    public boolean isBridge() {
        return this.method.isBridge();
    }

    public boolean isDefault() {
        return this.method.isDefault();
    }

    public boolean isSynthetic() {
        return this.method.isSynthetic();
    }

    public boolean isVarArgs() {
        return this.method.isVarArgs();
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return this.method.isAnnotationPresent(annotationClass);
    }

    public String toGenericString() {
        return this.method.toGenericString();
    }

    @Override
    public String toString() {
        return this.method.toString();
    }
}
