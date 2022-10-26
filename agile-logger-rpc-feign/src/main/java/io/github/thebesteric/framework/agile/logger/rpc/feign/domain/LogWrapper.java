package io.github.thebesteric.framework.agile.logger.rpc.feign.domain;

import io.github.thebesteric.framework.agile.logger.spring.domain.Parent;
import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;
import lombok.Getter;
import lombok.Setter;

/**
 * Log Wrapper
 *
 * @author Eric Joe
 * @version 1.0
 */
@Getter
@Setter
public class LogWrapper {
    private RequestLog requestLog;
    private Parent parent;

    public LogWrapper(RequestLog requestLog, Parent parent) {
        this.requestLog = requestLog;
        this.parent = parent;
    }
}
