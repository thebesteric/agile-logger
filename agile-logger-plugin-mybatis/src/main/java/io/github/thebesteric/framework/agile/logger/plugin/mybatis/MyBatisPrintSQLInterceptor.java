package io.github.thebesteric.framework.agile.logger.plugin.mybatis;

import io.github.thebesteric.framework.agile.logger.commons.utils.*;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.domain.ExecuteInfo;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.spring.domain.Parent;
import io.github.thebesteric.framework.agile.logger.spring.processor.RecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Intercepts({
        // @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
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

        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Object parameterObject = null;
        if (args.length > 1) {
            parameterObject = args[1];
        }

        String targetId = mappedStatement.getId();
        String className = targetId.substring(0, targetId.lastIndexOf("."));
        String methodName = targetId.substring(targetId.lastIndexOf(".") + 1);
        Class<?> targetClass = Class.forName(className);
        // ParameterMap parameterMap = mappedStatement.getParameterMap();
        // for (Method declaredMethod : targetClass.getDeclaredMethods()) {
        //     System.out.println(declaredMethod.getName());
        // }
        // Method targetMethod = targetClass.getDeclaredMethod(methodName, parameterMap.getType());
        // System.out.println(targetMethod);

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
            // return invocation.proceed();
        }


        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        String sql = boundSql.getSql();
        sql = sql.replaceAll("\t", " ")
                .replaceAll("\r", " ")
                .replaceAll("\n", " ")
                .replaceAll("\\s{2,}", " ")
                .replace("( ", "(")
                .replace(" )", ")")
                .trim();

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        List<String> fieldNames = parameterMappings.stream().map(ParameterMapping::getProperty).collect(Collectors.toList());


        Map<String, Object> parameters = new HashMap<>();
        if (parameterObject != null) {
            Class<?> currentClass = parameterObject.getClass();
            do {
                Field[] declaredFields = currentClass.getDeclaredFields();
                if (CollectionUtils.isNotEmpty(declaredFields)) {
                    for (Field declaredField : declaredFields) {
                        if (fieldNames.contains(declaredField.getName())) {
                            declaredField.setAccessible(true);
                            parameters.put(StringUtils.camelToUnderline(declaredField.getName()), declaredField.get(parameterObject));
                        }
                    }
                }
                currentClass = currentClass.getSuperclass();
            } while (currentClass != Object.class);
        }

        // insert into t (id, name) values (?, ?)
        String[] columnNames = new String[fieldNames.size()];
        if (sql.contains("?")) {
            sql = sql.replace("?", "%s");
            String str = sql.substring(sql.indexOf("(") + 1, sql.indexOf(")"));
            columnNames = Arrays.stream(str.split(",")).map(s -> s.trim().replace("`", "")).toArray(String[]::new);
        }

        Object[] columnValues = new Object[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            Object obj = parameters.get(columnNames[i]);
            if (obj instanceof String) {
                columnValues[i] = "'" + obj + "'";
            } else if (obj instanceof Date) {
                columnValues[i] = "'" + DateUtils.format((Date) obj, "yyyy-MM-dd HH:mm:ss") + "'";
            } else if (obj instanceof Boolean) {
                columnValues[i] = ((Boolean) obj) ? 1 : 0;
            } else {
                columnValues[i] = obj;
            }
        }

        sql = String.format(sql, columnValues);

        System.out.println("sql = " + sql);

        String durationTag = DurationWatcher.start();
        DurationWatcher.Duration duration = DurationWatcher.get(durationTag);
        Parent parent = AgileLoggerContext.getParent();

        InvokeLog invokeLog = new InvokeLog();
        invokeLog = InvokeLog.builder(invokeLog)
                .parentId(parent != null ? parent.getId() : null)
                .trackId(TransactionUtils.get())
                .createdAt(duration.getStartTime())
                //.tag(agileLoggerContext.getProperties().getPlugins().getMyBatis().getDefaultTag())
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
            DurationWatcher.stop(durationTag);
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
        // INSERT INTO table (id, name) VALUES (?, ?)
        return preparedStatementSql;
    }
}
