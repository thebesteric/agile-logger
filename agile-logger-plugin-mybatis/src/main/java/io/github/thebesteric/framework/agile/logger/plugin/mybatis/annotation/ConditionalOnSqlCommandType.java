package io.github.thebesteric.framework.agile.logger.plugin.mybatis.annotation;

import io.github.thebesteric.framework.agile.logger.core.domain.SqlCommandType;
import io.github.thebesteric.framework.agile.logger.plugin.mybatis.condition.SqlCommandTypeCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional({SqlCommandTypeCondition.class})
public @interface ConditionalOnSqlCommandType {
    SqlCommandType value();
}
