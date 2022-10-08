package io.github.thebesteric.framework.agile.logger.commons.exception;

/**
 * IllegalArgumentException
 *
 * @author Eric Joe
 * @version 1.0
 */
public class IllegalArgumentException extends java.lang.IllegalArgumentException {
    public IllegalArgumentException() {
        super("Illegal argument exception");
    }
    public IllegalArgumentException(String message, Object... params) {
        super(String.format(message, params));
    }
}
