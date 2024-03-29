package io.github.thebesteric.framework.agile.logger.commons.exception;

/**
 * DataNotExistsException
 *
 * @author Eric Joe
 * @version 1.0
 */
public class DataNotExistsException extends RuntimeException {
    public DataNotExistsException() {
        super("Data not exists exception");
    }

    public DataNotExistsException(String message, Object... params) {
        super(String.format(message, params));
    }
}
