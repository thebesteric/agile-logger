package io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.annotation;

import io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.VersionAdapter;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Versioner {
    String REQUEST_METHOD_NAME = "request";
    String RESPONSE_METHOD_NAME = "response";

    /**
     * Specify the VersionAdapter
     */
    @SuppressWarnings("rawtypes")
    Class<? extends VersionAdapter> type();
}
