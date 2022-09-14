package agile.logger.example.web.quickstart;

import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
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
@AgileLogger(tag = "service")
public class TestService {

    @Autowired
    private TestAdapter testAdapter;

    public String sayHello(String name) {
        return testAdapter.sayHello(name, new Date());
    }
}
