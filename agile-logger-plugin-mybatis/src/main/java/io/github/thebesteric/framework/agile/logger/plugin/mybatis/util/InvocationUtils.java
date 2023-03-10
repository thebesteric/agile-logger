package io.github.thebesteric.framework.agile.logger.plugin.mybatis.util;

import io.github.thebesteric.framework.agile.logger.commons.utils.CollectionUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.ReflectUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InvocationUtils {
    public static Class<?> getInvocationClass(Invocation invocation) {
        Method method = invocation.getMethod();

        String targetId = getMappedStatement(invocation).getId();
        String targetClassName = targetId.substring(0, targetId.lastIndexOf("."));
        String targetMethodName = targetId.substring(targetId.lastIndexOf(".") + 1);

        Class<?> targetClass = ReflectUtils.getClassForName(targetClassName);
        if (targetClass != null) {
            Method[] declaredMethods = targetClass.getDeclaredMethods();
            if (CollectionUtils.isNotEmpty(declaredMethods)) {
                List<String> declaredMethodNames = Arrays.stream(declaredMethods).map(Method::getName).collect(Collectors.toList());
                if (!declaredMethodNames.contains(targetMethodName)) {
                    targetClassName = method.getDeclaringClass().getName();
                    targetMethodName = method.getName();
                }
            }
        }

        return null;
    }

    public static Method getInvocationMethod(Invocation invocation) {
        Method method = invocation.getMethod();

        String targetId = getMappedStatement(invocation).getId();
        String targetClassName = targetId.substring(0, targetId.lastIndexOf("."));
        String targetMethodName = targetId.substring(targetId.lastIndexOf(".") + 1);

        Class<?> targetClass = ReflectUtils.getClassForName(targetClassName);
        if (targetClass != null) {
            Method[] declaredMethods = targetClass.getDeclaredMethods();
            if (CollectionUtils.isNotEmpty(declaredMethods)) {
                List<String> declaredMethodNames = Arrays.stream(declaredMethods).map(Method::getName).collect(Collectors.toList());
                if (!declaredMethodNames.contains(targetMethodName)) {
                    targetClassName = method.getDeclaringClass().getName();
                    targetMethodName = method.getName();
                }
            }
        }

        return null;
    }

    public static MappedStatement getMappedStatement(Invocation invocation) {
        Object[] args = invocation.getArgs();
        return  (MappedStatement) args[0];
    }
}
