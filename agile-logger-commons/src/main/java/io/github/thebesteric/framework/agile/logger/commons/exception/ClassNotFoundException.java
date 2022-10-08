package io.github.thebesteric.framework.agile.logger.commons.exception;

/**
 * ClassNotFoundException
 *
 * @author Eric Joe
 * @version 1.0
 */
public class ClassNotFoundException extends java.lang.ClassNotFoundException {
    public ClassNotFoundException() {
        super("Class not found exception");
    }
    public ClassNotFoundException(String message, Object... params) {
        super(String.format(message, params));
    }
}
