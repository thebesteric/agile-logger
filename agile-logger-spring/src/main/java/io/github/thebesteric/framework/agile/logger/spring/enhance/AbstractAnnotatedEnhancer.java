package io.github.thebesteric.framework.agile.logger.spring.enhance;

import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.commons.utils.ReflectUtils;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Optional;

/**
 * AbstractAnnotatedEnhancer
 *
 * @author Eric Joe
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractAnnotatedEnhancer implements BeanPostProcessor {

    private static final String SPRING_CGLIB_PROXY_SEPARATOR = "$$";
    protected final Enhancer enhancer = new Enhancer();

    protected final AgileLoggerContext agileLoggerContext;

    protected boolean needEnhance(Class<?> clazz) {
        return ReflectUtils.anyAnnotationPresent(clazz, AgileLogger.class);
    }

    /**
     * 获取 bean 的 class
     *
     * @param beanClass {@link Class}
     * @return String
     * @author Eric
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
     * @author Eric
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
     * @author Eric
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
     * @author Eric
     */
    protected Object[] getConstructorArguments(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Map<String, ?> beansOfType = this.agileLoggerContext.getBeans(parameters[i].getType());
            if (beansOfType.size() == 1) {
                Optional<?> optional = beansOfType.values().stream().findFirst();
                args[i] = optional.get();
            } else {
                Object obj = this.agileLoggerContext.getBean(parameters[i].getName(), parameters[i].getType());
                args[i] = obj;
            }
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
     * @author Eric
     * @date 2021/5/26 17:57
     */
    protected synchronized Object copyProperties(Class<?> originClass, Object source, Object target) {
        Class<?> currentClass = originClass;
        do {
            for (Field sourceField : currentClass.getDeclaredFields()) {
                try {
                    ReflectUtils.set(sourceField, target, source);
                } catch (Exception ex) {
                    LoggerPrinter.error(log, ex.getMessage());
                }
            }
            currentClass = currentClass.getSuperclass();
        } while (currentClass != null && currentClass != Object.class);
        return target;
    }
}
