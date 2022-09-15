package io.github.thebesteric.framework.agile.logger.core.utils;

import io.github.thebesteric.framework.agile.logger.commons.utils.CollectionUtils;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.annotation.IgnoreMethod;
import io.github.thebesteric.framework.agile.logger.core.annotation.IgnoreMethods;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * AgileConditionChecker
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-07 23:28:15
 */
public class AgileConditionChecker {

    /**
     * Check if the method should be skipped
     *
     * @param type   class
     * @param method method
     * @return boolean
     */
    public static boolean shouldSkip(Class<?> type, Method method) {
        // Check type and method if it has @AgileLogger
        if (!type.isAnnotationPresent(AgileLogger.class) && !method.isAnnotationPresent(AgileLogger.class)) {
            return true;
        }

        // Check method if it has @IgnoreMethod annotation
        if (AgileConditionChecker.matchMethodOnIgnoreMethod(method)) {
            return true;
        }

        // Check @AgileLogger annotation on type if ignoreMethods attribute has values
        if (matchMethodOnIgnoreAttributes(method, type.getAnnotation(AgileLogger.class))) {
            return true;
        }

        // Check type if it has @IgnoreMethods annotation
        return AgileConditionChecker.matchMethodOnIgnoreMethods(type, method);
    }

    /**
     * Check @AgileLogger annotation if ignoreMethods attribute has values
     *
     * @param methodName  methodName
     * @param agileLogger {@link AgileLogger}
     * @return boolean
     */
    public static boolean matchMethodOnIgnoreAttributes(String methodName, AgileLogger agileLogger) {
        if (agileLogger != null && CollectionUtils.isEmpty(agileLogger.ignoreMethods())) {
            for (String im : agileLogger.ignoreMethods()) {
                if (methodName.equalsIgnoreCase(im)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check @AgileLogger annotation if ignoreMethods attribute has values
     *
     * @param method      method
     * @param agileLogger {@link AgileLogger}
     * @return boolean
     */
    public static boolean matchMethodOnIgnoreAttributes(Method method, AgileLogger agileLogger) {
        return matchMethodOnIgnoreAttributes(method.getName(), agileLogger);
    }

    /**
     * Check type if it has @IgnoreMethods annotation
     *
     * @param type       clazz
     * @param methodName methodName
     * @return boolean
     */
    public static boolean matchMethodOnIgnoreMethods(Class<?> type, String methodName) {
        if (type.isAnnotationPresent(IgnoreMethods.class)) {
            IgnoreMethods ignoreMethods = type.getAnnotation(IgnoreMethods.class);
            String[] ignoreMethodsArr = ignoreMethods.value();
            for (String ignoreMethodPattern : ignoreMethodsArr) {
                if (Pattern.matches(ignoreMethodPattern, methodName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check type if it has @IgnoreMethods annotation
     *
     * @param type   clazz
     * @param method method
     * @return boolean
     */
    public static boolean matchMethodOnIgnoreMethods(Class<?> type, Method method) {
        return matchMethodOnIgnoreMethods(type, method.getName());
    }

    /**
     * Check method if it has @IgnoreMethod annotation
     *
     * @param method method
     * @return boolean
     */
    public static boolean matchMethodOnIgnoreMethod(Method method) {
        return method.isAnnotationPresent(IgnoreMethod.class);
    }

}
