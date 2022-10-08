package agile.logger.example.web.quickstart.mock;

import agile.logger.example.web.quickstart.UserInfo;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.MockerAdapter;

/**
 * LoginMockAdapter
 *
 * @author Eric Joe
 * @version 1.0
 */
public class LoginMockAdapter implements MockerAdapter<UserInfo> {
    @Override
    public UserInfo mock() {
        return new UserInfo("lucy", "9988", "vip");
    }
}
