package io.github.thebesteric.framework.agile.logger.spring.enhance;

import io.github.thebesteric.framework.agile.logger.commons.utils.DurationWatcher;
import io.github.thebesteric.framework.agile.logger.commons.utils.ReflectUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.annotation.IgnoreMethod;
import io.github.thebesteric.framework.agile.logger.core.annotation.IgnoreMethods;
import io.github.thebesteric.framework.agile.logger.core.domain.ExecuteInfo;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.SyntheticAgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.TransactionUtils;
import io.github.thebesteric.framework.agile.logger.spring.processor.IgnoreMethodProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.ResponseSuccessDefineProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * AgileLoggerAnnotatedInterceptor
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-27 23:21:24
 */
public class AgileLoggerAnnotatedInterceptor implements MethodInterceptor {

    private final AgileLoggerContext agileLoggerContext;

    // Records the method regular expressions on the class that need to be ignored
    public static Map<String, Set<String>> ignoreMethodsCache = new HashMap<>();

    // Records whether the method requires an proxies or does not
    public static Map<String, Boolean> checkedMethodsCache = new HashMap<>(64);

    public AgileLoggerAnnotatedInterceptor(AgileLoggerContext agileLoggerContext) {
        this.agileLoggerContext = agileLoggerContext;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        // Check whether intercept is required
        // If parentId is null, the Controller layer is filtered
        String parentId = AgileLoggerContext.getParentId();
        if (!needProxy(method.getDeclaringClass(), method) || parentId == null) {
            AgileLoggerContext.setParentId(parentId);
            return methodProxy.invokeSuper(obj, args);
        }

        final String trackId = TransactionUtils.get();
        final SyntheticAgileLogger syntheticAgileLogger = new SyntheticAgileLogger(method);

        String durationTag = null;
        Object result = null;
        String exception = null;

        // Initialize the invokeLog
        InvokeLog invokeLog = new InvokeLog(parentId);
        AgileLoggerContext.setParentId(invokeLog.getLogId());

        try {
            // Star watcher
            durationTag = DurationWatcher.start();
            result = methodProxy.invokeSuper(obj, args);
            ResponseSuccessDefineProcessor responseSuccessDefineProcessor = agileLoggerContext.getResponseSuccessDefineProcessor();
            exception = responseSuccessDefineProcessor.processor(method, result);
            return result;
        } catch (Exception ex) {
            exception = ex.getMessage();
            syntheticAgileLogger.setLevel(InvokeLog.LEVEL_ERROR);
            throw ex;
        } finally {
            // End watcher
            DurationWatcher.Duration duration = DurationWatcher.stop(durationTag);

            // Create InvokeLog
            invokeLog = InvokeLog.builder(invokeLog)
                    .trackId(trackId)
                    .createdAt(duration.getStartTime())
                    .tag(syntheticAgileLogger.getTag())
                    .extra(syntheticAgileLogger.getExtra())
                    .executeInfo(new ExecuteInfo(method, args, duration))
                    .exception(exception)
                    .result(result)
                    .level(invokeLog.getException() != null ? InvokeLog.LEVEL_ERROR : syntheticAgileLogger.getLevel())
                    .build();

            this.agileLoggerContext.getCurrentRecordProcessor().processor(invokeLog);

        }
    }

    /**
     * Check whether intercept is required
     * <p>If true is returned, the proxy is required
     *
     * @param type   Class<?>
     * @param method Method
     * @return boolean
     */
    private boolean needProxy(Class<?> type, Method method) {
        String fullyQualifiedName = type.getName() + "#" + method.getName();
        Boolean methodStatus = checkedMethodsCache.get(fullyQualifiedName);
        if (methodStatus == null) {

            // Check IgnoreMethodProcessor in configuration
            IgnoreMethodProcessor ignoreMethodProcessor = agileLoggerContext.getIgnoreMethodProcessor();
            if (ignoreMethodProcessor.matching(IgnoreMethodProcessor.IgnoreMethod.builder()
                    .clazz(type).method(method).build())) {
                checkedMethodsCache.put(fullyQualifiedName, false);
                return false;
            }

            // Check the @IgnoreMethods on the class
            Set<String> ignoreMethods = ignoreMethodsCache.get(type.getName());
            if (ignoreMethods == null) {
                ignoreMethods = new HashSet<>();
                IgnoreMethods ignoreMethodsOnClassAnno = ReflectUtils.getAnnotation(type, IgnoreMethods.class);
                AgileLogger agileLoggerAnno = ReflectUtils.getAnnotation(type, AgileLogger.class);
                if (ignoreMethodsOnClassAnno != null) {
                    ignoreMethods.addAll(Arrays.asList(ignoreMethodsOnClassAnno.value()));
                }
                if (agileLoggerAnno != null) {
                    ignoreMethods.addAll(Arrays.asList(agileLoggerAnno.ignoreMethods()));
                }
                ignoreMethodsCache.put(type.getName(), ignoreMethods);
            }
            for (String ignoreMethodPattern : ignoreMethods) {
                if (Pattern.matches(ignoreMethodPattern, method.getName())) {
                    checkedMethodsCache.put(fullyQualifiedName, false);
                    return false;
                }
            }

            // Check the @IgnoreMethod on the method
            IgnoreMethod ignoreMethodAnno = method.getAnnotation(IgnoreMethod.class);
            if (ignoreMethodAnno != null) {
                checkedMethodsCache.put(fullyQualifiedName, false);
                return false;
            }

            // SkyWalking's setSkyWalkingDynamicField(Object arg) method is ignored
            if (agileLoggerContext.getProperties().isUseSkyWalkingTrace() && StringUtils.isEquals("setSkyWalkingDynamicField", method.getName())) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 && parameterTypes[0].getName().equals("java.lang.Object")) {
                    checkedMethodsCache.put(fullyQualifiedName, false);
                    return false;
                }
            }

            // Methods that require proxies
            checkedMethodsCache.put(fullyQualifiedName, true);
        }

        return methodStatus == null || methodStatus;
    }

}
