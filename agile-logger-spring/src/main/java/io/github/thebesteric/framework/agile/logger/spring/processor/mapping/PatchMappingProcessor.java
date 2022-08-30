package io.github.thebesteric.framework.agile.logger.spring.processor.mapping;

import io.github.thebesteric.framework.agile.logger.spring.processor.MappingProcessor;
import org.springframework.web.bind.annotation.PatchMapping;

import java.lang.reflect.Method;

/**
 * PatchMappingProcessor
 *
 * @author Eric Joe
 * @since 1.0
 */
public class PatchMappingProcessor implements MappingProcessor {

    private Method method;

    @Override
    public boolean supports(Method method) {
        this.method = method;
        return method.isAnnotationPresent(PatchMapping.class);
    }

    @Override
    public void processor(String[] classRequestMappingUrls) {
        doProcessor(classRequestMappingUrls, method, () -> method.getAnnotation(PatchMapping.class).value());
    }
}
