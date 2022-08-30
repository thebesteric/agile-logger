package io.github.thebesteric.framework.agile.logger.core.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {

    String name() default "";

    String type() default "varchar";

    int length() default 255;

}
