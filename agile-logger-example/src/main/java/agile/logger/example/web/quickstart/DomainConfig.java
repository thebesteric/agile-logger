package agile.logger.example.web.quickstart;

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
public class DomainConfig {
    private String omsUrl;
    private String ocUrl;
    private String icUrl;

    public String getOmsUrl() {
        return omsUrl;
    }

    public void setOmsUrl(String omsUrl) {
        this.omsUrl = omsUrl;
    }

    public String getOcUrl() {
        return ocUrl;
    }

    public void setOcUrl(String ocUrl) {
        this.ocUrl = ocUrl;
    }

    public String getIcUrl() {
        return icUrl;
    }

    public void setIcUrl(String icUrl) {
        this.icUrl = icUrl;
    }
}
