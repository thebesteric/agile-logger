package agile.logger.example.web.quickstart;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DomainConfig
 *
 * @author Eric Joe
 * @version 1.0
 */
@Component
@ConfigurationProperties(prefix = "domain")
@Data
public class DomainConfig {
    private String omsUrl;
    private String ocUrl;
    private String icUrl;
}
