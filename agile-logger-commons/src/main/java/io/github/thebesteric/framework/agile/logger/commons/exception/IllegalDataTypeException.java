package io.github.thebesteric.framework.agile.logger.commons.exception;

/**
 * IllegalDataTypeException
 *
 * @author Eric Joe
 * @version 1.0
 */
public class IllegalDataTypeException extends java.lang.IllegalArgumentException {
    public IllegalDataTypeException() {
        super("Illegal data type exception");
    }
    public IllegalDataTypeException(String message, Object... params) {
        super(String.format(message, params));
    }
}
