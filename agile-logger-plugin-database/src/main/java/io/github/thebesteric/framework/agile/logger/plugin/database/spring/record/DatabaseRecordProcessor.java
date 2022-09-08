package io.github.thebesteric.framework.agile.logger.plugin.database.spring.record;

import io.github.thebesteric.framework.agile.logger.commons.exception.UnsupportedModeException;
import io.github.thebesteric.framework.agile.logger.commons.utils.*;
import io.github.thebesteric.framework.agile.logger.core.annotation.Column;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import io.github.thebesteric.framework.agile.logger.plugin.database.spring.jdbc.AgileLoggerJdbcTemplate;
import io.github.thebesteric.framework.agile.logger.spring.processor.record.AbstractThreadPoolRecordProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;

/**
 * DatabaseRecordProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
public class DatabaseRecordProcessor extends AbstractThreadPoolRecordProcessor {

    private final AgileLoggerJdbcTemplate agileLoggerJdbcTemplate;

    public DatabaseRecordProcessor(AgileLoggerContext agileLoggerContext, AgileLoggerJdbcTemplate agileLoggerJdbcTemplate) {
        super(agileLoggerContext);
        this.agileLoggerJdbcTemplate = agileLoggerJdbcTemplate;
    }

    @Override
    public boolean supports(LogMode model) throws UnsupportedModeException {
        return agileLoggerJdbcTemplate != null && model != null && !model.getName().trim().equals("")
                && LogMode.DATABASE.getName().equalsIgnoreCase(model.getName());
    }

    @Override
    public void doProcess(InvokeLog invokeLog) throws Throwable {
        String tableName = getTableName(invokeLog.getClass());
        String[] insertProperties = getInsertProperties(invokeLog.getClass());
        String sql = "INSERT INTO " + tableName + " (" + insertProperties[0] + ") VALUES (" + insertProperties[1] + ")";

        this.agileLoggerJdbcTemplate.executeUpdate(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            List<Field> fields = ReflectUtils.getFields(invokeLog.getClass(), field -> !ReflectUtils.isStatic(field) && !ReflectUtils.isFinal(field));
            for (int i = 0; i < fields.size(); i++) {
                try {
                    Field field = fields.get(i);
                    field.setAccessible(true);
                    Object result = field.get(invokeLog);
                    Column column = field.getAnnotation(Column.class);
                    Column.Type type = column.type();

                    switch (type) {
                        case TINY_INT:
                        case SMALL_INT:
                        case INT:
                            ps.setInt(i + 1, result != null ? Integer.parseInt(result.toString()) : null);
                            break;
                        case BIG_INT:
                            ps.setLong(i + 1, result != null ? Long.parseLong(result.toString()) : null);
                            break;
                        case JSON:
                            ps.setString(i + 1, result != null ? JsonUtils.mapper.writeValueAsString(result) : null);
                            break;
                        case DATETIME:
                            ps.setString(i + 1, result != null ? DateUtils.format((Date) result, "yyyy-MM-dd HH:mm:ss") : null);
                            break;
                        default:
                            ps.setString(i + 1, result != null ? result.toString() : null);

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return ps;
        });
    }

    private String getTableName(Class<?> clazz) {
        String tableNamePrefix = this.agileLoggerContext.getProperties().getPlugins().getDatabase().getTableNamePrefix();
        return this.agileLoggerJdbcTemplate.getTableName(tableNamePrefix, clazz);
    }

    public String[] getInsertProperties(Class<?> clazz) {
        String[] arr = new String[2];
        StringBuilder fieldNames = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        List<Field> fields = ReflectUtils.getFields(clazz, field -> !ReflectUtils.isStatic(field) && !ReflectUtils.isFinal(field));
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            String fieldName = field.getName();
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                if (StringUtils.isNotEmpty(column.name())) {
                    fieldName = column.name();
                }
            }
            fieldNames.append(ObjectUtils.humpToUnderline(fieldName));
            placeholders.append("?");
            if (i != fields.size() - 1) {
                fieldNames.append(",");
                placeholders.append(",");
            }
        }
        arr[0] = fieldNames.toString();
        arr[1] = placeholders.toString();
        return arr;
    }
}
