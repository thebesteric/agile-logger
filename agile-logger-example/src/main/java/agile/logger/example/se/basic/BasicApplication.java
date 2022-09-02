package agile.logger.example.se.basic;

import agile.logger.example.domain.MathCalc;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLoggerEntrance;
import io.github.thebesteric.framework.agile.logger.core.utils.AgileLoggerHelper;

/**
 * BasicApplication
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-25
 */
@AgileLogger(tag = "application")
public class BasicApplication {

    // -javaagent:agile-logger-core-0.0.1.jar=stdout

    static {
        AgileLoggerHelper.builder()
                .enable(true)
                .build();
    }

    @AgileLoggerEntrance
    public static void main(String[] args) throws InterruptedException {
        MathCalc calc = new MathCalc();
        calc.add(1, 2);
        calc.minus(1, 2);
    }
}
