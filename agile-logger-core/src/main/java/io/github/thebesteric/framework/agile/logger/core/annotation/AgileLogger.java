package io.github.thebesteric.framework.agile.logger.core.annotation;

import io.github.thebesteric.framework.agile.logger.core.domain.AbstractEntity;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AgileLogger {

    /** Tag information */
    String tag() default AbstractEntity.TAG_DEFAULT;

    /** Extra information */
    String extra() default "";

    /**
     * The level of logging
     * <p>When an exception occurs, the log level is automatically changed to ERROR
     */
    String level() default AbstractEntity.LEVEL_INFO;

    /**
     * Methods to ignore
     * <p>Method overloading is not considered.
     * To consider method overloading, please use: {@link IgnoreMethod}
     *
     * @see IgnoreMethod
     */
    String[] ignoreMethods() default {};
}
