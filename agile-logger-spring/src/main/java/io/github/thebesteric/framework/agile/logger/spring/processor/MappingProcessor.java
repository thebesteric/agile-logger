package io.github.thebesteric.framework.agile.logger.spring.processor;

import io.github.thebesteric.framework.agile.logger.spring.wrapper.AbstractAgileLoggerFilter;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * Process the URL Mapping
 *
 * @author Eric Joe
 * @version 1.0
 */
public interface MappingProcessor {

    boolean supports(Method method);

    void processor(String[] classRequestMappingUrl);

    default void doProcessor(String[] classRequestMappingUrls, Method method, Supplier<String[]> supplier) {
        if (classRequestMappingUrls != null) {
            for (String classRequestMappingUrl : classRequestMappingUrls) {
                String[] methodRequestMappingUrls = supplier.get();
                if (!classRequestMappingUrl.startsWith("/")) {
                    classRequestMappingUrl = "/" + classRequestMappingUrl;
                }
                handlerMapping(classRequestMappingUrl, methodRequestMappingUrls, method);
            }
        }
    }

    default void handlerMapping(String classRequestMappingUrl, String[] methodRequestMappingUrls, Method method) {
        if (methodRequestMappingUrls.length == 0) {
            AbstractAgileLoggerFilter.URL_MAPPING.put(classRequestMappingUrl, method);
            if (!classRequestMappingUrl.endsWith("/")) {
                AbstractAgileLoggerFilter.URL_MAPPING.put(classRequestMappingUrl + "/", method);
            }
        } else {
            for (String methodRequestMappingUrl : methodRequestMappingUrls) {
                String url = classRequestMappingUrl + methodRequestMappingUrl;
                AbstractAgileLoggerFilter.URL_MAPPING.put(url, method);
            }
        }
    }
}
