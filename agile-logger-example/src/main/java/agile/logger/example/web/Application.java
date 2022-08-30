package agile.logger.example.web;

import io.github.thebesteric.framework.agile.logger.boot.starter.annotation.EnableAgileLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-16 15:35:23
 */
@SpringBootApplication(scanBasePackages = "agile.logger.example.web")
@EnableAgileLogger
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

}
