package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.annotation;

import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.MockerAdapter;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.NoMocker;

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

    String MOCK_METHOD_NAME = "mock";

    /**
     * enable or disabled
     */
    boolean enable() default true;

    /**
     * Specify the value
     */
    String value() default "";

    /**
     * Specify the address of the mock
     * e.g: http or local-address
     */
    String target() default "";

    /**
     * Specify the MockerAdapter
     */
    Class<? extends MockerAdapter> type() default NoMocker.class;

    /**
     * Use cache
     */
    boolean cache() default true;
}
