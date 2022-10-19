package io.github.thebesteric.framework.agile.logger.spring.enhance;

import io.github.thebesteric.framework.agile.logger.commons.utils.*;
import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.annotation.IgnoreMethod;
import io.github.thebesteric.framework.agile.logger.core.annotation.IgnoreMethods;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.SyntheticAgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.domain.SpringSyntheticAgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.VersionerInfo;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.MockProcessor;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.annotation.Mocker;
import io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.annotation.Versioner;
import io.github.thebesteric.framework.agile.logger.spring.processor.IgnoreMethodProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.InvokeLoggerProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.ResponseSuccessDefineProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

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

    // Records whether the method requires a proxies or does not
    public static Map<Integer, Boolean> checkedMethodsCache = new HashMap<>(64);

    public AgileLoggerAnnotatedInterceptor(AgileLoggerContext agileLoggerContext) {
        this.agileLoggerContext = agileLoggerContext;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        // Check whether intercept is required
        // If parentId is null, the Controller layer is filtered
        String parentId = AgileLoggerContext.getParentId();
        if (!needLogIntercept(method) || parentId == null) {
            AgileLoggerContext.setParentId(parentId);

            // Mocker: invoke mocker result if you need to
            Object result = invokeMockerIfNecessary(method, args);
            if (result != null) {
                return result;
            }

            // Versioner: invoke versioner request method if you need to
            VersionerInfo versionerInfo = invokeVersionerRequestMethodIfNecessary(method, args);
            // Invoke
            result = methodProxy.invokeSuper(obj, args);
            // Versioner: invoke versioner response method if you need to
            return invokeVersionerResponseMethodIfNecessary(versionerInfo, result);
        }

        String logId = AgileContext.idGenerator.generate();
        AgileLoggerContext.setParentId(logId);

        final SyntheticAgileLogger syntheticAgileLogger = SpringSyntheticAgileLogger.getSpringSyntheticAgileLogger(method);

        String durationTag = null;
        Object result = null;
        String exception = null;
        boolean mock = false;

        try {

            // Star watcher
            durationTag = DurationWatcher.start();

            // Mocker: invoke mocker result if you need to
            result = invokeMockerIfNecessary(method, args);
            if (result != null) {
                mock = true;
            }

            VersionerInfo versionerInfo = null;
            // Without mock
            if (!mock) {
                // Versioner: invoke versioner request method if you need to
                versionerInfo = invokeVersionerRequestMethodIfNecessary(method, args);

                // Invoke if without mock
                result = methodProxy.invokeSuper(obj, args);
            }

            // Process non-program exceptions, For example: code != 200
            ResponseSuccessDefineProcessor responseSuccessDefineProcessor = agileLoggerContext.getResponseSuccessDefineProcessor();
            exception = responseSuccessDefineProcessor.processor(method, result);

            // Versioner: invoke versioner response method if you need to
            return mock ? result : invokeVersionerResponseMethodIfNecessary(versionerInfo, result);
        } catch (Exception ex) {
            exception = ExceptionUtils.getSimpleMessage(ex, 1024);
            syntheticAgileLogger.setLevel(InvokeLog.LEVEL_ERROR);
            throw ex;
        } finally {
            // End watcher
            DurationWatcher.Duration duration = DurationWatcher.stop(durationTag);
            // Build InvokeLog
            InvokeLoggerProcessor invokeLoggerProcessor = agileLoggerContext.getInvokeLoggerProcessor();
            InvokeLog invokeLog = invokeLoggerProcessor.processor(logId, parentId, method, args, result, exception, duration, mock);
            // Record InvokeLog
            this.agileLoggerContext.getCurrentRecordProcessor().processor(invokeLog);
        }
    }

    /**
     * Check whether intercept is required
     * <p>If true is returned, the proxy is required
     *
     * @param method Method
     * @return boolean
     */
    private boolean needLogIntercept(Method method) {
        int key = SignatureUtils.methodSignatureHashCode(method);
        Boolean methodStatus = checkedMethodsCache.get(key);
        if (methodStatus == null) {

            Class<?> type = method.getDeclaringClass();

            // Check IgnoreMethodProcessor in configuration
            IgnoreMethodProcessor ignoreMethodProcessor = agileLoggerContext.getIgnoreMethodProcessor();
            if (ignoreMethodProcessor.matching(IgnoreMethodProcessor.IgnoreMethod.builder()
                    .clazz(type).method(method).build())) {
                checkedMethodsCache.put(key, false);
                return false;
            }

            // Check there are no @AgileLogger on method and type
            AgileLogger agileLoggerAnnoOnType = ReflectUtils.getAnnotation(type, AgileLogger.class);
            AgileLogger agileLoggerAnnoOnMethod = ReflectUtils.getAnnotation(method, AgileLogger.class);
            if (agileLoggerAnnoOnType == null && agileLoggerAnnoOnMethod == null) {
                // Check is @RestController or @Controller
                if (!ReflectUtils.anyAnnotationPresent(type, RestController.class, Controller.class)) {
                    checkedMethodsCache.put(key, false);
                    return false;
                }
            }

            // Check the @IgnoreMethods on the class
            Set<String> ignoreMethods = ignoreMethodsCache.get(type.getName());
            if (ignoreMethods == null) {
                ignoreMethods = new HashSet<>();
                IgnoreMethods ignoreMethodsOnClassAnno = ReflectUtils.getAnnotation(type, IgnoreMethods.class);
                if (ignoreMethodsOnClassAnno != null) {
                    ignoreMethods.addAll(Arrays.asList(ignoreMethodsOnClassAnno.value()));
                }
                if (agileLoggerAnnoOnType != null) {
                    ignoreMethods.addAll(Arrays.asList(agileLoggerAnnoOnType.ignoreMethods()));
                }
                ignoreMethodsCache.put(type.getName(), ignoreMethods);
            }
            for (String ignoreMethodPattern : ignoreMethods) {
                if (Pattern.matches(ignoreMethodPattern, method.getName())) {
                    checkedMethodsCache.put(key, false);
                    return false;
                }
            }

            // Check the @IgnoreMethod on the method
            IgnoreMethod ignoreMethodAnno = method.getAnnotation(IgnoreMethod.class);
            if (ignoreMethodAnno != null) {
                checkedMethodsCache.put(key, false);
                return false;
            }

            // SkyWalking's setSkyWalkingDynamicField(Object arg) method is ignored
            if (agileLoggerContext.getProperties().getConfig().getTrack().isUseSkyWalkingTrace()
                    && StringUtils.isEquals("setSkyWalkingDynamicField", method.getName())) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 && parameterTypes[0].getName().equals("java.lang.Object")) {
                    checkedMethodsCache.put(key, false);
                    return false;
                }
            }

            // Methods that require proxies
            checkedMethodsCache.put(key, true);
        }

        return methodStatus == null || methodStatus;
    }

    /**
     * Versioner: invoke versioner request method if you need to
     *
     * @param method method
     * @param args   args
     * @return {@link VersionerInfo}
     */
    private VersionerInfo invokeVersionerRequestMethodIfNecessary(Method method, Object[] args) throws Exception {
        VersionerInfo versionerInfo = null;
        if (method.isAnnotationPresent(Versioner.class)) {
            versionerInfo = new VersionerInfo(method, args);
            VersionerInfo.MethodInfo requestMethodInfo = versionerInfo.getRequestMethodInfo();
            if (requestMethodInfo != null) {
                requestMethodInfo.invoke();
            }
        }
        return versionerInfo;
    }

    /**
     * Versioner: invoke versioner response method if you need to
     *
     * @param versionerInfo {@link VersionerInfo}
     * @param result        result
     * @return Object
     */
    private Object invokeVersionerResponseMethodIfNecessary(VersionerInfo versionerInfo, Object result) throws Exception {
        if (versionerInfo != null) {
            VersionerInfo.MethodInfo responseMethodInfo = versionerInfo.getResponseMethodInfo(result);
            if (responseMethodInfo != null) {
                result = responseMethodInfo.invoke();
            }
        }
        return result;
    }

    /**
     * Mocker: invoke mocker result if you need to
     *
     * @param method method
     * @return Object
     */
    private Object invokeMockerIfNecessary(Method method, Object[] args) throws Throwable {
        Object mockResult = null;
        if (method.isAnnotationPresent(Mocker.class) && agileLoggerContext.getProperties().getConfig().getMock().isEnable()) {
            Mocker mocker = method.getAnnotation(Mocker.class);
            if (mocker != null && mocker.enable()) {
                MockProcessor currentMethodMockProcessor = agileLoggerContext.getCurrentMethodMockProcessor(mocker, method);
                if (currentMethodMockProcessor != null) {
                    mockResult = currentMethodMockProcessor.process(mocker, method, args);
                }
            }
        }
        return mockResult;
    }

}
