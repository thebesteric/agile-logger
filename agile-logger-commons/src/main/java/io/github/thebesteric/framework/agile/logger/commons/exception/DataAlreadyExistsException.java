package io.github.thebesteric.framework.agile.logger.commons.exception;

/**
 * 异常：数据已存在
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022/7/22
 */
public class DataAlreadyExistsException extends RuntimeException {
    public DataAlreadyExistsException() {
        super("Data already exists exception");
    }
    public DataAlreadyExistsException(String message, Object... params) {
        super(String.format(message, params));
    }
}
