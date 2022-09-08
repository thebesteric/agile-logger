package io.github.thebesteric.framework.agile.logger.plugin.database.spring.domain;

import io.github.thebesteric.framework.agile.logger.commons.utils.ObjectUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.core.annotation.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;

/**
 * ColumnFieldMapper
 * <p>The new field only contains fieldMapper.
 * <p>The update field contains columnMapper and fieldMapper.
 *
 * @author Eric Joe
 * @version 1.0
 */
@Getter
@Setter
public class ColumnFieldMapper {

    private String tableName;
    private Mapper columnMapper;
    private FieldMapper fieldMapper;

    @Getter
    @Setter
    public static class FieldMapper extends Mapper {
        private Field field;

        public FieldMapper(Field field) {
            super(field.getAnnotation(Column.class), field);
            this.field = field;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Mapper {
        private String columnName;
        private String columnComment;
        private String columnType;
        private int columnSize;
        private boolean columnNullable;

        public Mapper(Column column, Field field) {
            this.columnName = StringUtils.isNotEmpty(column.name()) ? column.name() : ObjectUtils.humpToUnderline(field.getName());
            this.columnComment = column.comment();
            this.columnType = column.unsigned() ? column.type().getName().toUpperCase() + " unsigned" : column.type().getName().toUpperCase();
            this.columnSize = column.length();
            this.columnNullable = column.nullable();
        }

        public Mapper(String columnName, String columnComment, String columnType, int columnSize, boolean columnNullable) {
            this.columnName = columnName;
            this.columnComment = columnComment;
            this.columnType = columnType.toUpperCase();
            this.columnSize = columnSize;
            this.columnNullable = columnNullable;
        }
    }
}
