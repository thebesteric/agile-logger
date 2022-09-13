package io.github.thebesteric.framework.agile.logger.rpc.feign.domain;

import feign.RequestTemplate;
import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;
import lombok.Getter;
import lombok.Setter;

/**
 * RequestLogInfo
 *
 * @author Eric Joe
 * @version 1.0
 */
@Getter
@Setter
public class RequestLogInfo {
    private RequestLog requestLog;
    private RequestTemplate requestTemplate;

    public RequestLogInfo(RequestLog requestLog, RequestTemplate requestTemplate) {
        this.requestLog = requestLog;
        this.requestTemplate = requestTemplate;
    }
}
