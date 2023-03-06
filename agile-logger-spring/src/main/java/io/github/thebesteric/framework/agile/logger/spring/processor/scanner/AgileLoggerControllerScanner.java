package io.github.thebesteric.framework.agile.logger.spring.processor.scanner;

import io.github.thebesteric.framework.agile.logger.commons.utils.ClassPathScanner;
import io.github.thebesteric.framework.agile.logger.commons.utils.ClassUtils;
import io.github.thebesteric.framework.agile.logger.spring.processor.MappingProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.List;

/**
 * AgileLoggerControllerScanner
 * <p>scan @Controller and @RestController
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022/7/25
 */
@RequiredArgsConstructor
public class AgileLoggerControllerScanner implements ClassPathScanner {

    private final List<MappingProcessor> mappingProcessors;

    @Override
    public void processClassFile(String className) {
        Class<?> clazz = ClassUtils.forName(className, false);

        // Collect the urls from the Controller
        if (clazz != null && (clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(RestController.class))) {
            RequestMapping classRequestMapping = clazz.getAnnotation(RequestMapping.class);
            String[] classRequestMappingUrls = {""};
            if (classRequestMapping != null) {
                classRequestMappingUrls = classRequestMapping.value();
                if (classRequestMappingUrls.length == 0) {
                    classRequestMappingUrls = classRequestMapping.path();
                }
            }
            for (Method method : clazz.getDeclaredMethods()) {
                for (MappingProcessor mappingProcessor : mappingProcessors) {
                    if (mappingProcessor.supports(method)) {
                        mappingProcessor.processor(classRequestMappingUrls);
                        break;
                    }
                }
            }
        }

    }
}
