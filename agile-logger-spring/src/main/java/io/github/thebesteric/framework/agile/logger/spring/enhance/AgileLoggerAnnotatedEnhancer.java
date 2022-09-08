package io.github.thebesteric.framework.agile.logger.spring.enhance;

import io.github.thebesteric.framework.agile.logger.commons.utils.ClassUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Callback;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AgileLoggerAnnotatedEnhancer
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-27 23:00:33
 */
@Slf4j
public class AgileLoggerAnnotatedEnhancer extends AbstractAnnotatedEnhancer {

    public AgileLoggerAnnotatedEnhancer(AgileLoggerContext agileLoggerContext) {
        super(agileLoggerContext);
        if (!agileLoggerContext.getProperties().isEnable()) {
            return;
        }
        enhancer.setNamingPolicy((prefix, source, key, names) -> {
            if (prefix == null) {
                prefix = "net.sf.cglib.empty.Object";
            } else if (prefix.startsWith("java")) {
                prefix = "$" + prefix;
            }
            StringBuilder builder = new StringBuilder(prefix);
            builder.append("$$");
            builder.append(source.substring(source.lastIndexOf('.') + 1));
            builder.append("ByAgileLogger");
            builder.append("$$");
            builder.append(Integer.toHexString(key.hashCode()));
            String attempt = builder.toString();
            int index = 2;
            while (names.evaluate(attempt))
                attempt = builder + "_" + index++;
            return attempt;
        });
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {

        Class<?> beanClass = bean.getClass();

        // Beans that does not require an agent
        if (!agileLoggerContext.getProperties().isEnable() || !needEnhance(beanClass)) {
            return bean;
        }

        String originClassName = getOriginClassName(beanClass);
        Class<?> originClass = ClassUtils.forName(originClassName, false);

        // Maybe something error about the origin class
        if (originClass == null) {
            LoggerPrinter.debug(log, "Maybe something error about the {}", originClassName);
            return bean;
        }

        // Proxy the class if there is an @AgileLogger on the type
        if (originClass.isAnnotationPresent(AgileLogger.class)) {
            return enhance(originClass, new AgileLoggerAnnotatedInterceptor(agileLoggerContext), beanName, bean);
        }

        // Proxy the class if any of the methods have @AgileLogger on them
        for (Method declaredMethod : beanClass.getDeclaredMethods()) {
            // Proxy the class if there is an @AgileLogger on the method
            if (declaredMethod.isAnnotationPresent(AgileLogger.class)) {
                return enhance(originClass, new AgileLoggerAnnotatedInterceptor(agileLoggerContext), beanName, bean);
            }
        }

        return bean;
    }

    public Object enhance(Class<?> originClass, Callback callback, String beanName, Object bean) {

        enhancer.setSuperclass(originClass);
        enhancer.setCallback(callback);

        Object object = bean;
        // 1. Deal the no argument constructor
        for (Constructor<?> constructor : originClass.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                object = enhancer.create();
                break;
            }
        }

        // 2. Deal the has arguments constructor
        if (object == bean) {
            Map<Constructor<?>, Integer> constructors = new HashMap<>();
            for (Constructor<?> constructor : originClass.getDeclaredConstructors()) {
                int parameterCount = constructor.getParameterCount();
                if (parameterCount != 0) {
                    constructors.put(constructor, parameterCount);
                }
            }

            // All constructors are in reverse order of the number of parameters
            List<? extends Constructor<?>> sortedConstructors = constructors.entrySet().stream()
                    .sorted((o1, o2) -> o2.getValue() - o1.getValue()).map(Map.Entry::getKey).collect(Collectors.toList());


            // 1. There is only one constructor that checks whether the parameter can be injected
            if (sortedConstructors.size() == 1) {
                Constructor<?> constructor = sortedConstructors.get(0);
                Object[] args = getConstructorArguments(constructor);
                object = enhancer.create(constructor.getParameterTypes(), args);
            }
            // 2. If there are more than one constructor, filter out the constructor that contains the @Autowired annotation
            else {
                // Gets the @Autowired constructor with the highest number of arguments
                Constructor<?> longestConstructor = null;
                for (Constructor<?> constructor : sortedConstructors) {
                    if (constructor.isAnnotationPresent(Autowired.class)) {
                        longestConstructor = constructor;
                        break;
                    }
                }
                assert longestConstructor != null;
                Object[] args = getConstructorArguments(longestConstructor);
                object = enhancer.create(longestConstructor.getParameterTypes(), args);
            }
        }

        // Attribute assignment
        if (object != bean) {
            this.agileLoggerContext.getBeanFactory().registerSingleton(beanName, copyProperties(originClass, bean, object));
        }

        return object;
    }
}
