package io.github.thebesteric.framework.agile.logger.commons.exception;

/**
 * ParseErrorException
 *
 * @author Eric Joe
 * @version 1.0
 */
public class ParseErrorException extends RuntimeException {
    public ParseErrorException() {
        super("Parse error exception");
    }
    public ParseErrorException(String message, Object... params) {
        super(String.format(message, params));
    }
}
