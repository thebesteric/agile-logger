package io.github.thebesteric.framework.agile.logger.spring.processor.mapping;

import io.github.thebesteric.framework.agile.logger.commons.utils.CollectionUtils;
import io.github.thebesteric.framework.agile.logger.spring.processor.MappingProcessor;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.reflect.Method;

/**
 * GetMappingProcessor
 *
 * @author Eric Joe
 * @since 1.0
 */
public class GetMappingProcessor implements MappingProcessor {

    private Method method;

    @Override
    public boolean supports(Method method) {
        this.method = method;
        return method.isAnnotationPresent(GetMapping.class);
    }

    @Override
    public void processor(String[] classRequestMappingUrls) {
        doProcessor(classRequestMappingUrls, method, () -> {
            GetMapping annotation = method.getAnnotation(GetMapping.class);
            String[] value = annotation.value();
            if (CollectionUtils.isEmpty(value)) {
                value = annotation.path();
            }
            return value;
        });
    }
}
