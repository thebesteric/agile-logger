package io.github.thebesteric.framework.agile.logger.plugin.mybatis.annotation;

import io.github.thebesteric.framework.agile.logger.core.domain.SqlCommandType;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Whether the current sql command is supported
 */
public class SqlCommandTypeCondition implements Condition {
    @Override
    public boolean matches(@Nonnull ConditionContext context, @Nonnull AnnotatedTypeMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionalOnSqlCommandType.class.getName());
        SqlCommandType annotatedSqlCommandType = null;
        if (annotationAttributes != null) {
            annotatedSqlCommandType = (SqlCommandType) annotationAttributes.get("value");
        }
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        if (beanFactory != null) {
            AgileLoggerSpringProperties properties = beanFactory.getBean(AgileLoggerSpringProperties.class);
            AgileLoggerSpringProperties.MyBatis myBatis = properties.getPlugins().getMyBatis();

            SqlCommandType[] sqlCommandTypes = myBatis.getCommandTypes();
            if (sqlCommandTypes != null) {
                for (SqlCommandType sqlCommandType : sqlCommandTypes) {
                    if (annotatedSqlCommandType == sqlCommandType) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
