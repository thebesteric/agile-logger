package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.annotation;

import java.lang.annotation.*;

/**
 * Mocker
 *
 * @author Eric Joe
 * @since 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mocker {
    boolean enable() default true;

    String value() default "";

    /**
     * Specify the address of the mock
     * e.g: http or local-address
     */
    String target() default "";

    /**
     * Use cache
     */
    boolean cache() default true;
}
