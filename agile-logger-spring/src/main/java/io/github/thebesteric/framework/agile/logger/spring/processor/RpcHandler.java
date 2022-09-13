package io.github.thebesteric.framework.agile.logger.spring.processor;

/**
 * RpcHandler
 *
 * @author Eric Joe
 * @version 1.0
 */
public interface RpcHandler {
    void log(String configKey, String format, Object... args);
}
