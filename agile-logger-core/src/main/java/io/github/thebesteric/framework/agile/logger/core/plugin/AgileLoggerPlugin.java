package io.github.thebesteric.framework.agile.logger.core.plugin;

import io.github.thebesteric.framework.agile.logger.core.domain.LogMode;

/**
 * <p>Service Provider Interface
 * <p>For JavaSE Project plugin
 *
 * @author Eric Joe
 * @version 1.0
 */
@FunctionalInterface
public interface AgileLoggerPlugin {
    boolean service(LogMode logMode);
}
