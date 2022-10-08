package io.github.thebesteric.framework.agile.logger.spring.enhance;

import io.github.thebesteric.framework.agile.logger.commons.utils.DurationWatcher;
import io.github.thebesteric.framework.agile.logger.commons.utils.ReflectUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.SignatureUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.annotation.IgnoreMethod;
import io.github.thebesteric.framework.agile.logger.core.annotation.IgnoreMethods;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.SyntheticAgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.domain.SpringSyntheticAgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.domain.VersionerInfo;
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
        if (!needProxy(method) || parentId == null) {
            AgileLoggerContext.setParentId(parentId);
            return methodProxy.invokeSuper(obj, args);
        }

        // Versioner: handler request
        VersionerInfo versionerInfo = null;
        if (method.isAnnotationPresent(Versioner.class)) {
            Versioner versioner = method.getAnnotation(Versioner.class);
            versionerInfo = new VersionerInfo(versioner, args);
            VersionerInfo.MethodInfo requestMethodInfo = versionerInfo.getRequestMethodInfo();
            if (requestMethodInfo != null) {
                requestMethodInfo.invoke();
            }
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

            // Invoke
            result = methodProxy.invokeSuper(obj, args);

            // Process non-program exceptions, For example: code != 200
            ResponseSuccessDefineProcessor responseSuccessDefineProcessor = agileLoggerContext.getResponseSuccessDefineProcessor();
            exception = responseSuccessDefineProcessor.processor(method, result);

            // Mocker
            if (method.isAnnotationPresent(Mocker.class) && agileLoggerContext.getProperties().getConfig().isMockEnable()) {
                Mocker mocker = method.getAnnotation(Mocker.class);
                if (mocker != null && mocker.enable()) {
                    MockProcessor currentMethodMockProcessor = agileLoggerContext.getCurrentMethodMockProcessor(mocker, method);
                    if (currentMethodMockProcessor != null) {
                        Object mockResult = currentMethodMockProcessor.process(mocker, method);
                        if (mockResult != null) {
                            result = mockResult;
                            mock = true;
                        }
                    }
                }
            }

            // Versioner: handler response
            if (!mock && versionerInfo != null) {
                VersionerInfo.MethodInfo responseMethodInfo = versionerInfo.getResponseMethodInfo(result);
                if (responseMethodInfo != null) {
                    result = responseMethodInfo.invoke();
                }
            }

            return result;
        } catch (Exception ex) {
            exception = ex.getMessage();
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
    private boolean needProxy(Method method) {
        String fullyQualifiedName = SignatureUtils.methodSignature(method);
        ;
        Boolean methodStatus = checkedMethodsCache.get(fullyQualifiedName);
        if (methodStatus == null) {

            Class<?> type = method.getDeclaringClass();

            // Check IgnoreMethodProcessor in configuration
            IgnoreMethodProcessor ignoreMethodProcessor = agileLoggerContext.getIgnoreMethodProcessor();
            if (ignoreMethodProcessor.matching(IgnoreMethodProcessor.IgnoreMethod.builder()
                    .clazz(type).method(method).build())) {
                checkedMethodsCache.put(fullyQualifiedName, false);
                return false;
            }

            // Check there are no @AgileLogger on method and type
            AgileLogger agileLoggerAnnoOnType = ReflectUtils.getAnnotation(type, AgileLogger.class);
            AgileLogger agileLoggerAnnoOnMethod = ReflectUtils.getAnnotation(method, AgileLogger.class);
            if (agileLoggerAnnoOnType == null && agileLoggerAnnoOnMethod == null) {
                // Check is @RestController or @Controller
                if (!ReflectUtils.anyAnnotationPresent(type, RestController.class, Controller.class)) {
                    checkedMethodsCache.put(fullyQualifiedName, false);
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
