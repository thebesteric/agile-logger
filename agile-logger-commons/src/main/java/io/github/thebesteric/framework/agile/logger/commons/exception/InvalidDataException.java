package io.github.thebesteric.framework.agile.logger.commons.exception;

/**
 * InvalidDataException
 *
 * @author Eric Joe
 * @version 1.0
 */
public class InvalidDataException extends RuntimeException {
    public InvalidDataException() {
        super("Invalid data exception");
    }
    public InvalidDataException(String message, Object... params) {
        super(String.format(message, params));
    }
}
