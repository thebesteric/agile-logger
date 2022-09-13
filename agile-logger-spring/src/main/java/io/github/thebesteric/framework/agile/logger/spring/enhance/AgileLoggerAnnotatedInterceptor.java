package io.github.thebesteric.framework.agile.logger.spring.enhance;

import io.github.thebesteric.framework.agile.logger.commons.utils.DurationWatcher;
import io.github.thebesteric.framework.agile.logger.commons.utils.ReflectUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.core.annotation.IgnoreMethod;
import io.github.thebesteric.framework.agile.logger.core.annotation.IgnoreMethods;
import io.github.thebesteric.framework.agile.logger.core.domain.ExecuteInfo;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.SyntheticAgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.TransactionUtils;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.processor.IgnoreMethodProcessor;
import io.github.thebesteric.framework.agile.logger.spring.processor.ResponseSuccessDefineProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * AgileLoggerAnnotatedInterceptor
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-27 23:21:24
 */
public class AgileLoggerAnnotatedInterceptor implements MethodInterceptor {

    private final AgileLoggerContext agileLoggerContext;

    public AgileLoggerAnnotatedInterceptor(AgileLoggerContext agileLoggerContext) {
        this.agileLoggerContext = agileLoggerContext;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        // Check whether intercept is required
        if (!check(method.getDeclaringClass(), method)) {
            return methodProxy.invokeSuper(obj, args);
        }

        final String trackId = TransactionUtils.get();
        final SyntheticAgileLogger syntheticAgileLogger = new SyntheticAgileLogger(method);

        String durationTag = null;
        Object result = null;
        String exception = null;

        // Initialize the invokeLog
        InvokeLog invokeLog = new InvokeLog(AgileLoggerContext.getParentId());
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
     *
     * @param type   Class<?>
     * @param method Method
     * @return boolean
     */
    private boolean check(Class<?> type, Method method) {

        // Check IgnoreMethodProcessor in configuration
        IgnoreMethodProcessor ignoreMethodProcessor = agileLoggerContext.getIgnoreMethodProcessor();
        if (ignoreMethodProcessor.matching(IgnoreMethodProcessor.IgnoreMethod.builder()
                .clazz(type).method(method).build())) {
            return false;
        }

        // Check the @IgnoreMethods on the class
        IgnoreMethods ignoreMethodsOnClassAnno = ReflectUtils.getAnnotation(type, IgnoreMethods.class);
        if (ignoreMethodsOnClassAnno != null) {
            String[] ignoreMethods = ignoreMethodsOnClassAnno.value();
            for (String ignoreMethod : ignoreMethods) {
                if (ignoreMethod.equals(method.getName())) {
                    return false;
                }
            }
        }

        // Check the @IgnoreMethod on the method
        IgnoreMethod ignoreMethodAnno = method.getAnnotation(IgnoreMethod.class);
        if (ignoreMethodAnno != null) {
            return false;
        }

        // Check the IgnoreModifier type in the properties
        AgileLoggerSpringProperties properties = agileLoggerContext.getProperties();
        AgileLoggerSpringProperties.IgnoreModifiers.IgnoreModifier ignoreModifierByType = properties.getIgnoreModifiers().getType();
        if ((ignoreModifierByType.isPrivateModifier() && ReflectUtils.isPrivate(type))
                || (ignoreModifierByType.isStaticModifier() && ReflectUtils.isStatic(type))) {
            return false;
        }

        // Check the IgnoreModifier method in the properties
        AgileLoggerSpringProperties.IgnoreModifiers.IgnoreModifier ignoreModifierByMethod = properties.getIgnoreModifiers().getMethod();
        if ((ignoreModifierByMethod.isPrivateModifier() && ReflectUtils.isPrivate(method))
                || (ignoreModifierByMethod.isStaticModifier() && ReflectUtils.isStatic(method))) {
            return false;
        }

        // SkyWalking's setSkyWalkingDynamicField(Object arg) method is ignored
        if (agileLoggerContext.getProperties().isUseSkyWalkingTrace() && StringUtils.isEquals("setSkyWalkingDynamicField", method.getName())) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            return parameterTypes.length != 1 || !parameterTypes[0].getName().equals("java.lang.Object");
        }

        return true;
    }

}
