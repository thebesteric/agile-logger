package io.github.thebesteric.framework.agile.logger.core;

import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

/**
 * AgileApplication
 * java -javaagent:/${PATH}/agile-logger-core-0.0.1.jar=[logger/stdout/mysql] -jar ${YOUR_APP}.jar
 *
 * @author Eric Joe
 * @since 1.0
 */
public class AgileApplication {

    private static final Logger log = LoggerFactory.getLogger(AgileApplication.class);

    public static void premain(String args, Instrumentation inst) {
        AgileContext.enable = true;
        AgileContext.logMode = getLogMode(args);

        LoggerPrinter.info(log, "Agile premain agent is working");
    }

    public static LogMode getLogMode(String args) {
        LogMode mode = LogMode.STDOUT;
        if (args != null) {
            String[] params = args.split(",");
            mode = LogMode.getLogMode(params[0]);
        }
        return mode;
    }

}
