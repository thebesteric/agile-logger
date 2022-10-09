package agile.logger.example.web.quickstart;

import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.annotation.IgnoreMethods;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.annotation.Mocker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * TestService
 *
 * @author Eric Joe
 * @since 1.0
 */
@Service
// @AgileLogger(tag = "service", ignoreMethods = {"^foo.*"})
@AgileLogger(tag = "service")
@IgnoreMethods({"^bar.*"})
public class TestService {

    @Autowired
    private TestAdapter testAdapter;

    // @AgileLogger(tag = "service_sayHello", extra = "just say hello", level = AbstractEntity.LEVEL_DEBUG)
    public String sayHello(String name) {
        name = foo(name);
        return testAdapter.sayHello(name, new Date());
    }

    @Mocker("eric")
    public String sayHi(String name) {
        name = foo(name);
        return testAdapter.sayHi(name, new Date());
    }

    public String param(String name, int age) {
        return testAdapter.sayHi(name + ":" + age, new Date());
    }

    // @Versioner(type = LoginVersion.class)
    public UserInfo login(Identity identity) {
        return testAdapter.login(identity);
    }

    // @IgnoreMethod
    // @Mocker("eric")
    public String foo(String name) {
        return name;
    }
}
