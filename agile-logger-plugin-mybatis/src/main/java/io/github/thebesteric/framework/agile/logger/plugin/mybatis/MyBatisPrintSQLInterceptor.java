package io.github.thebesteric.framework.agile.logger.plugin.mybatis;

import io.github.thebesteric.framework.agile.logger.commons.utils.DurationWatcher;
import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.TransactionUtils;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.domain.ExecuteInfo;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.spring.domain.Parent;
import io.github.thebesteric.framework.agile.logger.spring.processor.RecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@RequiredArgsConstructor
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
})
public class MyBatisPrintSQLInterceptor implements Interceptor {

    @Getter
    private Properties properties;

    private final AgileLoggerContext agileLoggerContext;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Class<?> clazz = method.getDeclaringClass();

        List<String> ignoreMethodNames = new ArrayList<>();

        AgileLogger sqlRecorderOnClass = null;
        if (clazz.isAnnotationPresent(AgileLogger.class)) {
            sqlRecorderOnClass = clazz.getAnnotation(AgileLogger.class);
            // if (!sqlRecorderOnClass.enable()) {
            //     // execute
            //     return invocation.proceed();
            // }
        }

        AgileLogger sqlRecorderOnMethod = null;
        if (method.isAnnotationPresent(AgileLogger.class)) {
            sqlRecorderOnMethod = method.getAnnotation(AgileLogger.class);
            // if (!sqlRecorderOnMethod.enable()) {
            //     // execute
            //     return invocation.proceed();
            // }
        }

        if (sqlRecorderOnClass == null && sqlRecorderOnMethod == null) {
            // execute
            return invocation.proceed();
        }

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();

        String sql = boundSql.getSql();
        Map<String, Object> parameters = boundSql.getAdditionalParameters();


        System.out.println("sql = " + sql);
        System.out.println("parameters = " + parameters);

        String durationTag = DurationWatcher.start();
        DurationWatcher.Duration duration = DurationWatcher.get(durationTag);
        Parent parent = AgileLoggerContext.getParent();

        InvokeLog invokeLog = new InvokeLog();
        invokeLog = InvokeLog.builder(invokeLog)
                .parentId(parent != null ? parent.getId() : null)
                .trackId(TransactionUtils.get())
                .createdAt(duration.getStartTime())
                .tag(agileLoggerContext.getProperties().getPlugins().getMyBatis().getDefaultTag())
                .executeInfo(new ExecuteInfo(method, parameters.values().toArray(), duration))
                .result(fillParameters(sql, parameters))
                .build();

        Exception exception = null;
        try {
            // execute
            return invocation.proceed();
        } catch (Exception ex) {
            exception = ex;
        } finally {
            invokeLog.setException(exception == null ? null : exception.getMessage());
            invokeLog.setLevel(StringUtils.isNotEmpty(invokeLog.getException()) ? InvokeLog.LEVEL_ERROR : InvokeLog.LEVEL_INFO);

            // Record Log
            RecordProcessor currentRecordProcessor = agileLoggerContext.getCurrentRecordProcessor();
            currentRecordProcessor.processor(invokeLog);

            // Set Parent
            parent = new Parent(invokeLog.getLogId(), method, parameters.values().toArray());
            AgileLoggerContext.setParent(parent);

            // Clear
            DurationWatcher.clear();
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

    private String fillParameters(String preparedStatementSql, Map<String, Object> parameters) {
        // SELECT * FROM table WHERE t.id = ? and t.name = ?;
        // DELETE FROM table WHERE t.id = ? and t.name = ?;
        // UPDATE table SET t.id = ?, t.name = ? WHERE t.id = ? AND t.name = ?;
        return preparedStatementSql;
    }
}
