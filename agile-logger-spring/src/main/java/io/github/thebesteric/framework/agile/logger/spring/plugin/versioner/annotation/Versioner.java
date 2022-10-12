package io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.annotation;

import io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.VersionerAdapter;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Versioner {
    String REQUEST_METHOD_NAME = "request";
    String RESPONSE_METHOD_NAME = "response";

    /**
     * Specify the VersionerAdapter
     */
    @SuppressWarnings("rawtypes")
    Class<? extends VersionerAdapter> type();
}
