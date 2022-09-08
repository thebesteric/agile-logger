package io.github.thebesteric.framework.agile.logger.core;

import io.github.thebesteric.framework.agile.logger.commons.utils.ExceptionUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.commons.utils.ReflectUtils;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLoggerEntrance;
import io.github.thebesteric.framework.agile.logger.core.annotation.Column;
import io.github.thebesteric.framework.agile.logger.core.domain.ExecuteInfo;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.utils.AgileConditionChecker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * TestAspect
 *
 * @author Eric Joe
 * @since 1.0
 */
@Aspect
public class AgileAspect {

    private static final Logger log = LoggerFactory.getLogger(AgileAspect.class);

    /*
    execution(<@注解类型模式>? <修饰符模式>? <返回类型模式> <方法名模式>(<参数模式>) <异常模式>?)
     */

    @Pointcut("execution(* *.*(..)) && !execution(* io.github.thebesteric.framework.agile..*.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // The global switch
        if (!AgileContext.enable) {
            return joinPoint.proceed();
        }

        // Maybe Lambda expression
        if (joinPoint.getSignature().getModifiers() == 4106) {
            return joinPoint.proceed();
        }

        // Should skip this method
        if (AgileConditionChecker.shouldSkip(getClass(joinPoint), getMethod(joinPoint))) {
            return joinPoint.proceed();
        }

        // Fire & Bomb
        return fireAndBomb(joinPoint);
    }

    private Object fireAndBomb(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {

        // Create AgileContext for each thread
        AgileContext context = AgileContextUtils.create(joinPoint);
        // Get AgileLoggerContext for the joinPoint(method)
        AgileLoggerContext loggerContext = context.getAgileLoggerContext(joinPoint);

        Object result = null;
        // Fire...
        InvokeLog invokeLog = loggerContext.fire(context);
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            String exTitle = ExceptionUtils.getTitle(ex);
            String exCause = Objects.requireNonNull(ExceptionUtils.getMajorCause(ex)).toString();

            int limit = 1024;
            Field field = ReflectUtils.getField(InvokeLog.class, InvokeLog.EXCEPTION_FIELD_NAME);
            if (field != null && field.isAnnotationPresent(Column.class)) {
                limit = field.getAnnotation(Column.class).length();
            }
            invokeLog.setException(ExceptionUtils.getSimpleMessage(ex, limit));
            invokeLog.setLevel(InvokeLog.LEVEL_ERROR);
            throw new RuntimeException(exTitle + "\n\t" + exCause);
        } finally {
            ExecuteInfo executeInfo = invokeLog.getExecuteInfo();
            executeInfo.setDuration(System.currentTimeMillis() - executeInfo.getCreatedAt().getTime());
            invokeLog.setResult(result);

            // Bomb...
            loggerContext.bomb(context, invokeLog);

            // Shutdown ExecutorService
            isShouldShutdown(context, joinPoint);
        }

        return result;
    }

    private Class<?> getClass(ProceedingJoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringType();
    }

    private String getMethodName(ProceedingJoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }

    private Class<?>[] getMethodParameterTypes(ProceedingJoinPoint joinPoint) {
        return ((CodeSignature) joinPoint.getSignature()).getParameterTypes();
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        return getClass(joinPoint).getDeclaredMethod(getMethodName(joinPoint), getMethodParameterTypes(joinPoint));
    }

    private void isShouldShutdown(AgileContext context, ProceedingJoinPoint joinPoint) {
        try {
            Method method = getMethod(joinPoint);
            if (AgileContext.async &&
                    (method.isAnnotationPresent(AgileLoggerEntrance.class) || joinPoint.getSignature().getName().equals("main"))) {
                new Thread(() -> {
                    context.shutdownExecutorService();
                    LoggerPrinter.info(log, "Thread pool is closed, Agile Logger is about to exit");
                }, "shutdown-thread").start();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


}
