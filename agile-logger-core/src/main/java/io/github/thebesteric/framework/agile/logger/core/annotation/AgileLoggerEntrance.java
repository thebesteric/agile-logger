package io.github.thebesteric.framework.agile.logger.core.annotation;

import java.lang.annotation.*;

/**
 * AgileLoggerEntrance
 * <p>On the startup class, using in JavaSE project
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-12 15:30:26
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AgileLoggerEntrance {
}
