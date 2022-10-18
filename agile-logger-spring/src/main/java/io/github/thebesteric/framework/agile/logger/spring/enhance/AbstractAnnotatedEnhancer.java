package io.github.thebesteric.framework.agile.logger.spring.enhance;

import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.commons.utils.ReflectUtils;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.annotation.Mocker;
import io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.annotation.Versioner;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * AbstractAnnotatedEnhancer
 *
 * @author Eric Joe
 * @since 1.0
 */
@Slf4j
public abstract class AbstractAnnotatedEnhancer implements BeanPostProcessor {

    private static final String SPRING_CGLIB_PROXY_SEPARATOR = "$$";
    protected final Enhancer enhancer = new Enhancer();

    protected final AgileLoggerContext agileLoggerContext;

    public AbstractAnnotatedEnhancer(AgileLoggerContext agileLoggerContext) {
        this.agileLoggerContext = agileLoggerContext;
    }

    protected boolean needEnhance(Class<?> clazz) {
        return ReflectUtils.isAnnotationPresent(clazz, AgileLogger.class, true)
                || ReflectUtils.anyAnnotationPresent(clazz, RestController.class, Controller.class)
                || ReflectUtils.isAnnotationPresent(clazz, Versioner.class, true)
                || ReflectUtils.isAnnotationPresent(clazz, Mocker.class, true);
    }

    protected boolean isSpringInternalClass(Class<?> clazz) {
        return BasicErrorController.class == clazz;
    }

    /**
     * 获取 bean 的 class
     *
     * @param beanClass {@link Class}
     * @return String
     */
    protected String getOriginClassName(Class<?> beanClass) {
        String beanClassName = beanClass.getName();
        String originClassName = beanClassName;

        // Check if used Spring CGLIB. eg: Aspect
        if (beanClassName.contains(SPRING_CGLIB_PROXY_SEPARATOR)) {
            originClassName = beanClassName.substring(0, beanClassName.indexOf(SPRING_CGLIB_PROXY_SEPARATOR));
        }
        return originClassName;
    }

    /**
     * Check legal by Type
     * <p>The type must be public and not static or final
     *
     * @param clazz {@link Class}
     * @return boolean
     */
    protected boolean checkLegal(Class<?> clazz) {
        return clazz != null && ReflectUtils.isPublic(clazz) && !ReflectUtils.isStatic(clazz) && !ReflectUtils.isFinal(clazz);
    }

    /**
     * Check legal by Method
     * <p>The method type must be public and not static and final
     *
     * @param method {@link Method}
     * @return boolean
     */
    protected boolean checkLegal(Method method) {
        return method != null && ReflectUtils.isPublic(method) && !ReflectUtils.isStatic(method) && !ReflectUtils.isFinal(method);
    }

    /**
     * Get constructor arguments
     * <p>Look for beans to inject from Spring
     *
     * @param constructor {@link Constructor}
     * @return Object[] args
     */
    protected Object[] getConstructorArguments(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            args[i] = this.agileLoggerContext.getCorrectBean(parameters[i].getName(), parameters[i].getType());
        }
        return args;
    }

    /**
     * 属性拷贝
     *
     * @param originClass 原始类
     * @param source      原始对象
     * @param target      代理对象
     * @return java.lang.Object
     */
    protected synchronized Object copyProperties(Class<?> originClass, Object source, Object target) {
        Class<?> currentClass = originClass;
        do {
            for (Field sourceField : currentClass.getDeclaredFields()) {
                try {
                    if (ReflectUtils.isStatic(sourceField) && ReflectUtils.isFinal(sourceField)) {
                        continue;
                    }
                    ReflectUtils.setAccessible(sourceField);
                    // Copy property if source field value is not null
                    Object sourceFieldValue = sourceField.get(source);
                    Object targetFieldValue = sourceField.get(target);
                    if (sourceFieldValue == null && targetFieldValue == null) {
                        sourceFieldValue = this.agileLoggerContext.getCorrectBean(sourceField.getName(), sourceField.getType());
                        if (sourceFieldValue != null) {
                            // Join to source
                            sourceField.set(source, sourceFieldValue);
                        }
                    }
                    if (sourceFieldValue != targetFieldValue && sourceFieldValue != null) {
                        ReflectUtils.set(sourceField, target, source);
                    }
                } catch (Exception ex) {
                    LoggerPrinter.error(log, ex.getMessage());
                }
            }
            currentClass = currentClass.getSuperclass();
        } while (currentClass != null && currentClass != Object.class);

        return target;
    }
}
