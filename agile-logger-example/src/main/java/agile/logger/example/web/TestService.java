package agile.logger.example.web;

import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * TestService
 *
 * @author Eric Joe
 * @since 1.0
 */
@Service
@AgileLogger(tag = "[UPSTREAM]", extra = "just in test")
public class TestService {

    ApplicationContext context;
    ApplicationContext context1;

    public TestService(ApplicationContext context) {
    }

    @Autowired
    public TestService(ApplicationContext context, ApplicationContext context1) {
        this.context = context;
        this.context1 = context1;
    }

    @AgileLogger(tag = "[UPSTREAM-OUT]", extra = "just in test: add")
    public int add(int a, int b) {
        hello("agile");
        return a + b;
    }

    public String hello(String name) {
        return "hello " + name;
    }
}
