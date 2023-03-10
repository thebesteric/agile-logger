package io.github.thebesteric.framework.agile.logger.plugin.mybatis.processor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.thebesteric.framework.agile.logger.commons.utils.CollectionUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.ReflectUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.TransactionUtils;
import io.github.thebesteric.framework.agile.logger.core.domain.ExecuteInfo;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.MethodInfo;
import io.github.thebesteric.framework.agile.logger.plugin.mybatis.domain.SQLExecuteInfo;
import io.github.thebesteric.framework.agile.logger.plugin.mybatis.domain.StatementInfo;
import io.github.thebesteric.framework.agile.logger.spring.domain.Parent;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractStatementProcessor implements StatementProcessor {

    protected MappedStatement getMappedStatement(Invocation invocation) {
        Object[] args = invocation.getArgs();
        return (MappedStatement) args[0];
    }

    protected Object getParameterObject(Invocation invocation) {
        Object[] args = invocation.getArgs();
        Object parameterObject = null;
        if (args.length > 1) {
            parameterObject = args[1];
        }
        return parameterObject;
    }

    protected BoundSql getBoundSql(MappedStatement mappedStatement, Object parameterObject) {
        return mappedStatement.getBoundSql(parameterObject);
    }

    /**
     * Get field names
     *
     * @param boundSql boundSql
     * @return List<String>
     */
    protected List<String> getFieldNames(BoundSql boundSql) {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        return parameterMappings.stream().map((pm) -> {
            String property = pm.getProperty();
            String[] arr = property.split("\\.");
            return arr[arr.length - 1];
        }).collect(Collectors.toList());
    }

    /**
     * Get parameters
     *
     * @param fieldNames      fieldNames
     * @param parameterObject parameterObject
     * @param sqlCommandType  sqlCommandType
     * @return Map<String, Object>
     * @throws IllegalAccessException exception
     */
    protected Map<String, Object> getParameters(List<String> fieldNames, Object parameterObject, SqlCommandType sqlCommandType) throws IllegalAccessException {
        Map<String, Object> parameters = new HashMap<>();
        if (parameterObject != null) {
            if (parameterObject instanceof MapperMethod.ParamMap) {
                MapperMethod.ParamMap<?> paramMap = (MapperMethod.ParamMap<?>) parameterObject;

                if (SqlCommandType.SELECT == sqlCommandType) {
                    Object ew = paramMap.get("ew");
                    if (ew instanceof QueryWrapper) {
                        QueryWrapper<?> queryWrapper = (QueryWrapper<?>) ew;
                        Map<?, ?> pairs = queryWrapper.getParamNameValuePairs();
                        for (String fieldName : fieldNames) {
                            parameterObject = pairs.get(fieldName);
                            break;
                        }
                    }
                    Object param1 = paramMap.get("param1");

                    // Paging case
                    if (param1 instanceof Page) {
                        Page<?> page = (Page<?>) param1;
                        fieldNames.add(PAGE_OFFSET);
                        fieldNames.add(PAGE_LIMIT);
                        parameters.put(PAGE_OFFSET, (page.getCurrent() - 1) * page.getSize());
                        parameters.put(PAGE_LIMIT, page.getSize());
                    }

                } else {
                    Map.Entry<String, ?> entry = paramMap.entrySet().stream().filter(e -> e.getKey().equals("param1")).findFirst().orElse(null);
                    if (entry != null) {
                        parameterObject = entry.getValue();
                    }
                }
            }

            Class<?> currentClass = parameterObject.getClass();

            // Primitive or Primitive warp types or String type
            if (ReflectUtils.isPrimitiveOrWarp(currentClass) || ReflectUtils.isStringType(currentClass)) {
                Object value = ReflectUtils.parsePrimitiveOrWarpByType(parameterObject.toString(), currentClass);
                fieldNames.stream().findFirst().ifPresent(fieldName -> parameters.put(fieldName, value));
            }
            // POJO type
            else {
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
        }
        return parameters;
    }


    /**
     * Format SQL
     *
     * @param boundSql        boundSql
     * @param parameterObject parameterObject
     * @param sqlCommandType  sqlCommandType
     * @return String
     */
    protected String formatSql(BoundSql boundSql, Object parameterObject, SqlCommandType sqlCommandType) {
        String sql = boundSql.getSql();
        if (parameterObject != null) {
            if (parameterObject instanceof MapperMethod.ParamMap) {
                MapperMethod.ParamMap<?> paramMap = (MapperMethod.ParamMap<?>) parameterObject;
                if (SqlCommandType.SELECT == sqlCommandType) {
                    Object param1 = paramMap.get("param1");
                    // Paging case
                    if (param1 instanceof Page) {
                        sql += " LIMIT ? OFFSET ?";
                    }
                }
            }
        }
        return sql.replaceAll("\t", " ")
                .replaceAll("\r", " ")
                .replaceAll("\n", " ")
                .replaceAll("\\s{2,}", " ")
                .replace("( ", "(")
                .replace(" )", ")")
                .trim();
    }

    public StatementInfo buildStatementInfo(Invocation invocation) throws IllegalAccessException {
        MappedStatement mappedStatement = getMappedStatement(invocation);
        Object parameterObject = getParameterObject(invocation);
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        BoundSql boundSql = getBoundSql(mappedStatement, parameterObject);
        String sql = formatSql(boundSql, parameterObject, sqlCommandType);

        List<String> fieldNames = getFieldNames(boundSql);
        Map<String, Object> parameters = getParameters(fieldNames, parameterObject, sqlCommandType);

        return new StatementInfo(sql, parameters);
    }

    protected SQLExecuteInfo buildSQLExecuteInfo(String sql, Map<String, Object> parameters) {
        return new SQLExecuteInfo(sql, parameters);
    }

    @Override
    public InvokeLog processor(Invocation invocation) throws Throwable {
        StatementInfo statementInfo = buildStatementInfo(invocation);

        String sql = statementInfo.getSql();

        Map<String, Object> parameters = statementInfo.getParameters();
        LinkedHashMap<String, Object> parameterTypes = new LinkedHashMap<>();
        parameters.forEach((key, value) -> parameterTypes.put(key, value.getClass()));

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


        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setMethodName(targetMethodName);
        methodInfo.setReturnType(method.getReturnType().getName());
        methodInfo.setArguments(new LinkedHashMap<>(parameters));
        methodInfo.setSignatures(parameterTypes);

        ExecuteInfo executeInfo = new ExecuteInfo();
        executeInfo.setMethodInfo(methodInfo);

        executeInfo.setClassName(targetClassName);

        Parent parent = AgileLoggerContext.getParent();
        return InvokeLog.builder()
                .createdAt(System.currentTimeMillis())
                .parentId(parent == null ? null : parent.getId())
                .trackId(TransactionUtils.get())
                .executeInfo(executeInfo)
                .result(buildSQLExecuteInfo(sql, parameters))
                .build();
    }
}