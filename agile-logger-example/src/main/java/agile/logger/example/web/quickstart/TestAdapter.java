package agile.logger.example.web.quickstart;

import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@AgileLogger(tag = "adapter")
@RequiredArgsConstructor
public class TestAdapter extends BaseAdapter{

    private final DomainConfig domainConfig;

    @Value("${domain.oc-url}")
    private String ocUrl;

    public String sayHello(String name, Date date) {
        return "hello " + name + " at " + date.toLocaleString();
    }

    public String sayHi(String name, Date date) {
        return "hi " + name + " at " + date.toLocaleString();
    }

    // @Mocker(type = LoginMockAdapter.class)
    public UserInfo login(Identity identity) {
        return new UserInfo(identity.getUsername(), identity.getPassword(), identity.getIdentity());
    }
}
