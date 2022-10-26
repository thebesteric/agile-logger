package io.github.thebesteric.framework.agile.logger.rpc.rest.template.processor;

import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.core.domain.ExecuteInfo;
import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * ClientHttpResponseHandler
 *
 * @author Eric Joe
 * @version 1.0
 */
public class ClientHttpResponseHandler implements InvocationHandler {
    private static final String getBodyMethodName = "getBody";
    private final ClientHttpResponse clientHttpResponse;
    private byte[] body;

    private RequestLog requestLog;

    public ClientHttpResponseHandler(ClientHttpResponse clientHttpResponse, RequestLog requestLog) {
        this.clientHttpResponse = clientHttpResponse;
        this.requestLog = requestLog;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (StringUtils.equals(getBodyMethodName, method.getName())) {
            if (Objects.isNull(this.body)) {
                this.body = StreamUtils.copyToByteArray(this.clientHttpResponse.getBody());
            }

            // Build ExecuteInfo
            ExecuteInfo executeInfo = new ExecuteInfo(method, args);
            executeInfo.setDuration(requestLog.getDuration());
            requestLog.setExecuteInfo(executeInfo);

            return new ByteArrayInputStream(this.body);
        }
        return method.invoke(this.clientHttpResponse, args);
    }
}
