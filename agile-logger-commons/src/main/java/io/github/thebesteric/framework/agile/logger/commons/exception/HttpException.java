package io.github.thebesteric.framework.agile.logger.commons.exception;

/**
 * HttpException
 *
 * @author Eric Joe
 * @version 1.0
 */
public class HttpException extends RuntimeException {
    public HttpException() {
        super("Http request exception");
    }
    public HttpException(String message, Object... params) {
        super(String.format(message, params));
    }
}
