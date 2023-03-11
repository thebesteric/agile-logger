package io.github.thebesteric.framework.agile.logger.plugin.mybatis;

import io.github.thebesteric.framework.agile.logger.commons.utils.CollectionUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.DurationWatcher;
import io.github.thebesteric.framework.agile.logger.commons.utils.ReflectUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.core.AgileLoggerConstants;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.annotation.IgnoreMethod;
import io.github.thebesteric.framework.agile.logger.core.domain.AbstractEntity;
import io.github.thebesteric.framework.agile.logger.core.domain.ExecuteInfo;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.SyntheticAgileLogger;
import io.github.thebesteric.framework.agile.logger.plugin.mybatis.processor.StatementProcessor;
import io.github.thebesteric.framework.agile.logger.spring.domain.Parent;
import io.github.thebesteric.framework.agile.logger.spring.processor.RecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

@RequiredArgsConstructor
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class MyBatisPrintSQLInterceptor implements Interceptor {

    @Getter
    private Properties properties;
    private final AgileLoggerContext agileLoggerContext;
    private final List<StatementProcessor> statementProcessors;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        SyntheticAgileLogger syntheticAgileLogger = getSyntheticAgileLogger(invocation);
        String[] ignoreMethods = syntheticAgileLogger.getIgnoreMethods();
        if ((ignoreMethods != null && List.of(ignoreMethods).contains(syntheticAgileLogger.getMethod().getName()))
                || syntheticAgileLogger.getMethod().isAnnotationPresent(IgnoreMethod.class)
                || !syntheticAgileLogger.isMatched()) {
            return invocation.proceed();
        }

        // Record InvokeLog
        InvokeLog invokeLog = null;
        if (CollectionUtils.isNotEmpty(statementProcessors)) {
            for (StatementProcessor statementProcessor : statementProcessors) {
                if (statementProcessor.supports(invocation)) {
                    invokeLog = statementProcessor.processor(invocation);
                    break;
                }
            }
        }

        // The SqlCommandType is not configured in mybatis
        if (invokeLog == null) {
            return invocation.proceed();
        }

        // Start duration watcher
        String durationTag = DurationWatcher.start();
        DurationWatcher.Duration duration = DurationWatcher.get(durationTag);

        Exception exception = null;
        try {
            // execute
            return invocation.proceed();
        } catch (Exception ex) {
            exception = ex;
        } finally {
            // Stop duration watcher
            DurationWatcher.stop(durationTag);

            invokeLog.setTag(syntheticAgileLogger.getTag());
            invokeLog.setExtra(syntheticAgileLogger.getExtra());
            invokeLog.setException(exception == null ? null : exception.getMessage());
            invokeLog.setLevel(StringUtils.isNotEmpty(invokeLog.getException()) ? InvokeLog.LEVEL_ERROR : InvokeLog.LEVEL_INFO);

            ExecuteInfo executeInfo = invokeLog.getExecuteInfo();
            if (executeInfo != null) {
                executeInfo.setCreatedAt(duration.getStartTimeToDate());
                executeInfo.setDuration(duration.getDuration());
            }

            // Record Log
            RecordProcessor currentRecordProcessor = agileLoggerContext.getCurrentRecordProcessor();
            currentRecordProcessor.processor(invokeLog);

            // Set Parent
            AgileLoggerContext.setParent(new Parent(invokeLog.getLogId()));
        }
        return null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    private SyntheticAgileLogger getSyntheticAgileLogger(Invocation invocation) throws ClassNotFoundException {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];

        Method targetMethod = invocation.getMethod();

        String targetId = mappedStatement.getId();
        String targetClassName = targetId.substring(0, targetId.lastIndexOf("."));
        Class<?> targetClass = ReflectUtils.getClassForName(targetClassName);

        String targetMethodName = targetId.substring(targetId.lastIndexOf(".") + 1);

        Class<?> currentClass = targetClass;
        do {
            Method[] declaredMethods = currentClass.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if (targetMethodName.equals(declaredMethod.getName())) {
                    targetMethod = declaredMethod;
                    break;
                }
            }
            currentClass = targetClass.getSuperclass();
        } while (currentClass != null && currentClass != Object.class);

        SyntheticAgileLogger syntheticAgileLogger = new SyntheticAgileLogger(targetMethod, AgileLoggerConstants.PROPERTIES_PLUGINS_MYBATIS_DEFAULT_TAG);
        if (targetClass.isAnnotationPresent(AgileLogger.class)) {
            syntheticAgileLogger.setMatched(true);
            if (!targetMethod.isAnnotationPresent(AgileLogger.class)) {
                AgileLogger onType = targetClass.getAnnotation(AgileLogger.class);
                String tag = onType.tag();
                if (StringUtils.equals(onType.tag(), AbstractEntity.TAG_DEFAULT) || StringUtils.isEmpty(tag)) {
                    tag = AgileLoggerConstants.PROPERTIES_PLUGINS_MYBATIS_DEFAULT_TAG;
                }
                syntheticAgileLogger.setTag(tag);
                syntheticAgileLogger.setExtra(onType.extra());
                syntheticAgileLogger.setLevel(onType.level());
            }
        }

        return syntheticAgileLogger;
    }
}