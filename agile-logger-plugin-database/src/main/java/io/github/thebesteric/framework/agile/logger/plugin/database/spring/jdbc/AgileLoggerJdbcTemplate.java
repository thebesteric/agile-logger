package io.github.thebesteric.framework.agile.logger.plugin.database.spring.jdbc;

import io.github.thebesteric.framework.agile.logger.commons.utils.*;
import io.github.thebesteric.framework.agile.logger.core.annotation.Column;
import io.github.thebesteric.framework.agile.logger.core.annotation.Table;
import io.github.thebesteric.framework.agile.logger.plugin.database.spring.config.AgileLoggerDatabaseInitialization;
import io.github.thebesteric.framework.agile.logger.plugin.database.spring.domain.ColumnFieldMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AgileJdbcTemplate
 *
 * @author Eric Joe
 * @version 1.0
 */
public class AgileLoggerJdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(AgileLoggerDatabaseInitialization.class);

    public static final String TABLE_SEPARATOR = "_";

    private final JdbcTemplate jdbcTemplate;

    private static final Map<Class<?>, String> tableNamesCache = new HashMap<>();

    public AgileLoggerJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Create or Update Table
     *
     * @param tableNamePrefix tableNamePrefix
     * @param classes         classes
     * @throws SQLException ex
     */
    public void createOrUpdateTable(String tableNamePrefix, List<Class<?>> classes) throws SQLException {
        DataSource dataSource = jdbcTemplate.getDataSource();
        assert dataSource != null;

        try (Connection connection = dataSource.getConnection();) {
            DatabaseMetaData metaData = connection.getMetaData();
            for (Class<?> clazz : classes) {
                String tableName = getTableName(tableNamePrefix, clazz);
                ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
                if (!tables.next()) {
                    createTable(tableName, clazz);
                } else {
                    updateTable(tableName, clazz, metaData);
                }
            }
        }
    }

    /**
     * Update Table
     * <p>UPDATE RULE: If you want to update a changed field to database table, set version greater than 0
     * <p>INSERT RULE: If the new field does not exist in the database table, the insert operation is performed
     * <p>PS: If the field value does not change, the version number is invalid
     * <p>PS: If the field value changed but version is not set greater than 0, the field is treated as a new field
     *
     * @param tableName tableName
     * @param clazz     clazz
     * @param metaData  {@link DatabaseMetaData}
     * @throws SQLException ex
     */
    public void updateTable(String tableName, Class<?> clazz, DatabaseMetaData metaData) throws SQLException {
        List<Field> fields = ReflectUtils.getFields(clazz, field -> !ReflectUtils.isStatic(field) && !ReflectUtils.isFinal(field));
        ResultSet dataColumns = metaData.getColumns(null, "%", tableName, "%");

        List<ColumnFieldMapper> newColumnFieldMappers = new ArrayList<>();
        List<ColumnFieldMapper> updateColumnFieldMappers = new ArrayList<>();

        while (dataColumns.next()) {
            String tableColumnName = dataColumns.getString("COLUMN_NAME");

            // Skip ID column
            if ("id".equals(tableColumnName)) {
                continue;
            }

            // Find out new or update field
            ColumnFieldMapper columnFieldMapper = findNewOrUpdateField(tableName, fields, dataColumns);

            // Add to newColumnFieldMappers or updateColumnFieldMappers
            if (columnFieldMapper != null) {
                // Delete field that have been found
                Field field = columnFieldMapper.getFieldMapper().getField();
                fields.remove(field);
                if (field.getAnnotation(Column.class).version() == 0) {
                    newColumnFieldMappers.add(columnFieldMapper);
                } else {
                    updateColumnFieldMappers.add(columnFieldMapper);
                }
            }
        }

        // If there is a field, it must be a new field
        if (CollectionUtils.isNotEmpty(fields)) {
            fields.forEach(field -> {
                ColumnFieldMapper justFieldMapper = new ColumnFieldMapper();
                ColumnFieldMapper.FieldMapper fieldMapper = new ColumnFieldMapper.FieldMapper(field);
                justFieldMapper.setFieldMapper(fieldMapper);
                justFieldMapper.setTableName(tableName);
                newColumnFieldMappers.add(justFieldMapper);
            });
        }

        // Add Column
        if (CollectionUtils.isNotEmpty(newColumnFieldMappers)) {
            addColumn(tableName, newColumnFieldMappers);
        }

        // Update Column
        if (CollectionUtils.isNotEmpty(updateColumnFieldMappers)) {
            updateColumn(tableName, updateColumnFieldMappers);
        }

    }

    private void addColumn(String tableName, List<ColumnFieldMapper> columnFieldMappers) throws SQLException {
        for (ColumnFieldMapper mapper : columnFieldMappers) {
            ColumnFieldMapper.FieldMapper fieldMapper = mapper.getFieldMapper();

            String columnName = fieldMapper.getColumnName();
            String columnType = fieldMapper.getColumnType();
            int columnSize = fieldMapper.getColumnSize();
            String columnComment = StringUtils.nullToBlank(fieldMapper.getColumnComment());
            boolean columnNullable = fieldMapper.isColumnNullable();

            String dataType = columnType;
            if (Column.Type.VARCHAR.getName().equalsIgnoreCase(columnType)) {
                dataType += "(" + columnSize + ")";
            }
            String dataNullable = columnNullable ? "DEFAULT NULL" : "NOT NULL";

            String addSql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + dataType + " " + dataNullable;
            if(StringUtils.isNotEmpty(columnComment)) {
                addSql += " COMMENT '" + columnComment + "'";
            }
            executeUpdate(addSql);
            LoggerPrinter.info(log, "Update table {}: Add column name [{}] succeed", tableName.toUpperCase(), columnName);
        }
    }

    private void updateColumn(String tableName, List<ColumnFieldMapper> columnFieldMappers) throws SQLException {
        for (ColumnFieldMapper mapper : columnFieldMappers) {
            ColumnFieldMapper.FieldMapper fieldMapper = mapper.getFieldMapper();
            ColumnFieldMapper.Mapper columnMapper = mapper.getColumnMapper();

            String columnName = fieldMapper.getColumnName();
            String tableColumnName = columnMapper.getColumnName();
            String columnType = fieldMapper.getColumnType();
            String tableColumnType = columnMapper.getColumnType();
            int columnSize = fieldMapper.getColumnSize();
            int tableColumnSize = columnMapper.getColumnSize();
            String columnComment = fieldMapper.getColumnComment();
            String tableColumnComment = columnMapper.getColumnComment();
            boolean columnNullable = fieldMapper.isColumnNullable();
            boolean tableColumnNullable = columnMapper.isColumnNullable();

            // Update Column name
            if (StringUtils.notEquals(columnName, tableColumnName, true)) {
                String updateNameSql = "ALTER TABLE " + mapper.getTableName() + " CHANGE COLUMN " + tableColumnName + " " + columnName + " " + tableColumnType + "(" + tableColumnSize + ")";
                executeUpdate(updateNameSql);
                LoggerPrinter.info(log, "Update table {}: Change column name [{}] to [{}] succeed", tableName.toUpperCase(), tableColumnName, columnName);
            }

            // Update Column type
            if (StringUtils.notEquals(columnType, tableColumnType, true) || columnNullable != tableColumnNullable) {
                String nullable = columnNullable ? "DEFAULT NULL" : "NOT NULL";
                String tableNullable = tableColumnNullable ? "DEFAULT NULL" : "NOT NULL";
                throw new IllegalArgumentException(String.format("Column type [%s] cannot be changed to [%s]", columnType + "(" + nullable + ")", tableColumnType + "(" + tableNullable + ")"));
            }

            // Update Column size and comment
            if (StringUtils.equals(columnType, tableColumnType, true) && (columnSize != tableColumnSize || StringUtils.notEquals(columnComment, tableColumnComment, true))) {
                String oldColumnType = tableColumnType + "(" + tableColumnSize + ") COMMENT " + "'" + tableColumnComment + "'";
                String newColumnType = columnType + "(" + columnSize + ") COMMENT " + "'" + columnComment + "'";
                String updateNameSql = "ALTER TABLE " + mapper.getTableName() + " MODIFY COLUMN " + columnName + " " + newColumnType;
                executeUpdate(updateNameSql);
                LoggerPrinter.info(log, "Update table {}: Modify column [{}] to [{}] succeed", tableName.toUpperCase(), oldColumnType, newColumnType);
            }
        }
    }

    /**
     * Find out new or update field
     * <p>Returns NULL if no upsert is required
     *
     * @param fields      Class Fields
     * @param dataColumns Table columns
     * @return Field
     * @throws SQLException ex
     */
    private ColumnFieldMapper findNewOrUpdateField(String tableName, List<Field> fields, ResultSet dataColumns) throws SQLException {
        String tableColumnName = dataColumns.getString("COLUMN_NAME");
        String tableColumnComment = dataColumns.getString("REMARKS");
        String tableColumnType = dataColumns.getString("TYPE_NAME");
        int tableColumnSize = dataColumns.getInt("COLUMN_SIZE");
        boolean tableColumnNullable = dataColumns.getBoolean("NULLABLE");

        ColumnFieldMapper columnFieldMapper = null;
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            String currColumnName = StringUtils.isNotEmpty(column.name()) ? column.name() : ObjectUtils.humpToUnderline(field.getName());
            String currColumnType = column.unsigned() ? column.type().getName() + " unsigned" : column.type().getName();
            String currColumnComment = column.comment();
            boolean currColumnNullable = column.nullable();
            int currColumnSize = column.length();

            if (tableColumnName.equals(currColumnName) && tableColumnType.equalsIgnoreCase(currColumnType)
                    && tableColumnComment.equals(currColumnComment) && tableColumnNullable == currColumnNullable) {
                if (tableColumnType.equalsIgnoreCase(Column.Type.VARCHAR.getName())) {
                    if (tableColumnSize == currColumnSize) {
                        fields.remove(field);
                        break;
                    }
                } else {
                    fields.remove(field);
                    break;
                }
            }

            columnFieldMapper = new ColumnFieldMapper();
            ColumnFieldMapper.Mapper columnMapper = new ColumnFieldMapper.Mapper(tableColumnName, tableColumnComment, tableColumnType, tableColumnSize, tableColumnNullable);
            ColumnFieldMapper.FieldMapper fieldMapper = new ColumnFieldMapper.FieldMapper(field);
            columnFieldMapper.setColumnMapper(columnMapper);
            columnFieldMapper.setFieldMapper(fieldMapper);
            columnFieldMapper.setTableName(tableName);

            // This is a field to be updated, not a new field
            if (tableColumnName.equals(currColumnName)) {
                fields.remove(field);
                break;
            }
        }

        return columnFieldMapper;
    }

    /**
     * Create Table by Class
     * <p>Field must have @Column on it
     *
     * @param tableName tableName
     * @param clazz     class
     * @throws SQLException ex
     */
    public void createTable(String tableName, Class<?> clazz) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE `").append(tableName).append("` (");
        sb.append(" `id` int NOT NULL AUTO_INCREMENT,");
        List<Field> fields = ReflectUtils.getFields(clazz, field -> !ReflectUtils.isStatic(field) && !ReflectUtils.isFinal(field));
        List<String> uniqueFieldNames = new ArrayList<>();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            sb.append("`")
                    .append(column != null && StringUtils.isNotEmpty(column.name()) ? column.name() : ObjectUtils.humpToUnderline(field.getName()))
                    .append("` ")
                    .append(column != null ? column.type().getName() : "varchar");
            if (column != null) {
                if (Column.Type.VARCHAR == column.type()) {
                    sb.append("(").append(column.length()).append(")");
                }
                if (Column.Type.TINY_INT == column.type() || Column.Type.SMALL_INT == column.type() || Column.Type.INT == column.type() || Column.Type.BIG_INT == column.type()) {
                    if (column.unsigned()) {
                        sb.append(" ").append("unsigned");
                    }
                }
                if (column.nullable()) {
                    sb.append(" ").append("DEFAULT NULL");
                } else {
                    sb.append(" ").append("NOT NULL");
                }
                String comment = column.comment();
                if (StringUtils.isNotEmpty(comment)) {
                    sb.append(" ").append("COMMENT").append(" ").append("'").append(comment).append("'");
                }
            }
            sb.append(", ");
            if (column != null && column.unique()) {
                String uniqueFieldName = StringUtils.isNotEmpty(column.name()) ? column.name() : ObjectUtils.humpToUnderline(field.getName());
                uniqueFieldNames.add(uniqueFieldName);
            }
        }
        sb.append(" PRIMARY KEY (`id`),");

        for (int i = 0; i < uniqueFieldNames.size(); i++) {
            String uniqueName = uniqueFieldNames.get(i);
            sb.append(String.format(" UNIQUE KEY `%s` (`%s`) USING BTREE", "unique_" + uniqueName, uniqueName));
            if (i != uniqueFieldNames.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci");

        if (this.executeUpdate(sb.toString()) == 0) {
            LoggerPrinter.info(log, "Create table {} succeed.", tableName.toUpperCase());
        }
    }

    public int executeUpdate(String sql) throws SQLException {
        return executeUpdate(connection -> connection.prepareStatement(sql));
    }


    public int executeUpdate(PreparedStatementCreator preparedStatementCreator) throws SQLException {
        try (Connection connection = getDataSource().getConnection()) {
            return preparedStatementCreator.createPreparedStatement(connection).executeUpdate();
        }
    }

    public DataSource getDataSource() {
        return this.jdbcTemplate.getDataSource();
    }

    public String getTableName(String tableNamePrefix, Class<?> clazz) {
        String tableName = tableNamesCache.get(clazz);
        if (tableName != null) {
            return tableName;
        }
        synchronized (this) {
            tableName = tableNamePrefix;
            if (clazz.isAnnotationPresent(Table.class)) {
                Table table = clazz.getDeclaredAnnotation(Table.class);
                String name = table.name().trim();
                if (StringUtils.isNotEmpty(name)) {
                    tableName += TABLE_SEPARATOR + name;
                } else {
                    tableName += TABLE_SEPARATOR + ObjectUtils.humpToUnderline(clazz.getSimpleName());
                }
            } else {
                tableName += TABLE_SEPARATOR + ObjectUtils.humpToUnderline(clazz.getSimpleName());
            }
            tableNamesCache.put(clazz, tableName);
        }
        return tableName;
    }

}
