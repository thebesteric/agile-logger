package io.github.thebesteric.framework.agile.logger.commons.exception;

/**
 * PipelineNotDefinedException
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-08 14:18:28
 */
public class PipelineNotDefinedException extends RuntimeException {
    public PipelineNotDefinedException() {
        super("Pipeline is not defined");
    }
}
