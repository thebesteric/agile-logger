package agile.logger.example.web.quickstart.mock;

import agile.logger.example.web.quickstart.UserInfo;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.AbstractMockerAdapter;

import java.util.Arrays;

/**
 * LoginMockAdapter
 *
 * @author Eric Joe
 * @version 1.0
 */
public class LoginMockAdapter extends AbstractMockerAdapter<UserInfo> {

    @Override
    public UserInfo mock() {
        System.out.println(this.methodInfo);
        System.out.println(Arrays.toString(this.args));
        return new UserInfo("lucy", "9988", "vip");
    }
}
