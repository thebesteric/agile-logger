package io.github.thebesteric.framework.agile.logger.commons.exception;

/**
 * UnsupportedModeException
 *
 * @author Eric Joe
 */
public class UnsupportedModeException extends RuntimeException {

    public UnsupportedModeException(String mode) {
        super(String.format("Unsupported mode: %s", mode));
    }

}
