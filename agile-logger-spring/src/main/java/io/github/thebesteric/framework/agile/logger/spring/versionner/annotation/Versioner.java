package io.github.thebesteric.framework.agile.logger.spring.versionner.annotation;

import io.github.thebesteric.framework.agile.logger.spring.versionner.VersionAdapter;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Versioner {
    String REQUEST_METHOD_NAME = "request";
    String RESPONSE_METHOD_NAME = "response";

    @SuppressWarnings("rawtypes")
    Class<? extends VersionAdapter> type();
}
