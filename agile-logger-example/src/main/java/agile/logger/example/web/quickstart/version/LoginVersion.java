package agile.logger.example.web.quickstart.version;

import agile.logger.example.web.quickstart.TestController;
import agile.logger.example.web.quickstart.UserInfo;
import io.github.thebesteric.framework.agile.logger.spring.domain.R;
import io.github.thebesteric.framework.agile.logger.spring.versionner.AbstractVersionAdapter;

/**
 * LoginVersion
 *
 * @author Eric Joe
 * @version 1.0
 */
public class LoginVersion extends AbstractVersionAdapter<UserInfo, R> {
    @Override
    public void request(UserInfo userInfo) {
        userInfo.setPassword("******");
    }

    @Override
    public Object response(R result) {
        result.setMessage(TestController.version.get());
        return result;
    }
}
