package io.github.thebesteric.framework.agile.logger.core.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RewriteField {
    String value();
    String[] values() default {};
}
