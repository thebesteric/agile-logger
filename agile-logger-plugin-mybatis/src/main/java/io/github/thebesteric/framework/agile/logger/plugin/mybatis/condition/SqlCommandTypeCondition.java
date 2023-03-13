package io.github.thebesteric.framework.agile.logger.plugin.mybatis.condition;

import io.github.thebesteric.framework.agile.logger.core.domain.SqlCommandType;
import io.github.thebesteric.framework.agile.logger.plugin.mybatis.annotation.ConditionalOnSqlCommandType;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Whether the current sql command is supported
 */
@RequiredArgsConstructor
@Order
public class SqlCommandTypeCondition implements Condition {

    private static final String SQL_COMMAND_TYPES_PATH = "sourceflag.agile-logger.plugins.my-batis.command-types";
    private static final String DEFAULT_SQL_COMMAND_TYPES;

    static {
        AgileLoggerSpringProperties.MyBatis myBatis = new AgileLoggerSpringProperties.MyBatis();
        List<String> list = Arrays.stream(myBatis.getCommandTypes()).map(Enum::name).collect(Collectors.toList());
        DEFAULT_SQL_COMMAND_TYPES = String.join(",", list);
    }

    @Override
    public boolean matches(@Nonnull ConditionContext context, @Nonnull AnnotatedTypeMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionalOnSqlCommandType.class.getName());
        SqlCommandType annotatedSqlCommandType = null;
        if (annotationAttributes != null) {
            annotatedSqlCommandType = (SqlCommandType) annotationAttributes.get("value");
        }

        String sqlCommandTypeArr = context.getEnvironment().getProperty(SQL_COMMAND_TYPES_PATH, DEFAULT_SQL_COMMAND_TYPES);
        if (StringUtils.isNotEmpty(sqlCommandTypeArr)) {
            List<String> sqlCommandTypes = Arrays.stream(sqlCommandTypeArr.split(",")).map(String::trim).collect(Collectors.toList());
            for (String sqlCommandType : sqlCommandTypes) {
                if (annotatedSqlCommandType == SqlCommandType.of(sqlCommandType)) {
                    return true;
                }
            }
        }
        return false;
    }
}
