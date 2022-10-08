package io.github.thebesteric.framework.agile.logger.commons.exception;

/**
 * PipelineDoesNotHaveEnoughNodesException
 *
 * @author Eric Joe
 * @version 1.0
 */
public class PipelineDoesNotHaveEnoughNodesException extends RuntimeException {
    public PipelineDoesNotHaveEnoughNodesException() {
        super("The pipeline requires at least one head and one tail nods");
    }
}
