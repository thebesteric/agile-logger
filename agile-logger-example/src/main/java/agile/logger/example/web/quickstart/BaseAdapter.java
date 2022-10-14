package agile.logger.example.web.quickstart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.Random;

/**
 * BaseAdapter
 *
 * @author Eric Joe
 * @version 1.0
 */
public class BaseAdapter {
    @Autowired
    private Environment environment;
    @Autowired
    private CommAdapter commAdapter;
    @Value("${domain.oms-url}")
    private String omsUrl;
    private Random random = new Random();
}
