package agile.logger.example.web.quickstart.version;

import io.github.thebesteric.framework.agile.logger.commons.utils.VersionUtils;
import io.github.thebesteric.framework.agile.logger.spring.domain.R;
import io.github.thebesteric.framework.agile.logger.spring.plugin.versioner.AbstractVersionerAdapter;

import java.util.Map;

/**
 * LoginMapVersion
 *
 * @author Eric Joe
 * @version 1.0
 */
public class MapVersion extends AbstractVersionerAdapter<Map<String, Object>, R> {
    @Override
    public void request(Map<String, Object> map) {
        map.put("version", VersionUtils.get());
        System.out.println(map);
    }
}
