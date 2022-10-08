package io.github.thebesteric.framework.agile.logger.commons.exception;

/**
 * PipelineNotDefinedException
 *
 * @author Eric Joe
 * @version 1.0
 */
public class PipelineNotDefinedException extends RuntimeException {
    public PipelineNotDefinedException() {
        super("Pipeline is not defined");
    }
}
