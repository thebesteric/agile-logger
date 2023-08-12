package io.github.thebesteric.framework.agile.logger.boot.starter.annotation;

import io.github.thebesteric.framework.agile.logger.boot.starter.marker.AgileLoggerMarker;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AgileLoggerMarker.class)
@Documented
public @interface EnableAgileLogger {
    String[] basePackages() default {};
}
