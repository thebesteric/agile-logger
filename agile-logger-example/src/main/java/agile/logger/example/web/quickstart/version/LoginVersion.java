package agile.logger.example.web.quickstart.version;

import agile.logger.example.web.quickstart.Identity;
import agile.logger.example.web.quickstart.UserInfo;
import io.github.thebesteric.framework.agile.logger.commons.utils.VersionUtils;
import io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.AbstractVersionAdapter;

/**
 * LoginVersion
 *
 * @author Eric Joe
 * @version 1.0
 */
public class LoginVersion extends AbstractVersionAdapter<Identity, UserInfo> {
    @Override
    public void request(Identity identity) {
        if (VersionUtils.compareLessThan(VersionUtils.get(), "9.1.0")) {
            identity.setIdentity("customer");
        } else {
            identity.setIdentity("vip");
        }
    }

    @Override
    public UserInfo response(UserInfo userInfo) {
        userInfo.setPassword("******");
        return userInfo;
    }
}
