package io.github.thebesteric.framework.agile.logger.commons.exception;

/**
 * PipelineDoesNotHaveEnoughNodesException
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-11 17:18:45
 */
public class PipelineDoesNotHaveEnoughNodesException extends RuntimeException {
    public PipelineDoesNotHaveEnoughNodesException() {
        super("The pipeline requires at least one head and one tail nods");
    }
}
