package agile.logger.example.web.quickstart.mock;

import agile.logger.example.web.quickstart.UserInfo;
import io.github.thebesteric.framework.agile.logger.spring.domain.R;
import io.github.thebesteric.framework.agile.logger.spring.plugin.mocker.MethodsMockerAdapter;

/**
 * MutilMethodMockAdapter
 *
 * @author Eric Joe
 * @version 1.0
 */
public class MultiMethodMockAdapter extends MethodsMockerAdapter {

    public UserInfo mock1() {
        return new UserInfo("mock1", "***", "hello mock1");
    }

    public R mock2() {
        return R.success(new UserInfo("mock2", "***", "hello mock2"));
    }

}
