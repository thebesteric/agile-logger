package agile.logger.example.web;

import agile.logger.example.web.quickstart.TestService;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * FooUtils
 *
 * @author Eric Joe
 * @since 1.0
 */
@Component
@AgileLogger
public class FooUtils {

    @Autowired
    private AgileLoggerSpringProperties properties;

    String foo() {
        return "foo";
    }

    @Autowired(required = false)
    public FooUtils(TestService testService) {
    }

    @Autowired(required = false)
    public FooUtils(TestService testService, TestService testService1) {
    }

    public FooUtils(TestService testService, TestService testService1, TestService testService2) {
    }

}
