package io.github.thebesteric.framework.agile.logger.rpc.rest.template.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.thebesteric.framework.agile.logger.commons.utils.*;
import io.github.thebesteric.framework.agile.logger.core.domain.ExecuteInfo;
import io.github.thebesteric.framework.agile.logger.spring.domain.Parent;
import io.github.thebesteric.framework.agile.logger.spring.domain.R;
import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;
import io.github.thebesteric.framework.agile.logger.spring.processor.ResponseSuccessDefineProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.Inet4Address;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * RestTemplateClientHttpRequestInterceptor
 *
 * @author Eric Joe
 * @version 1.0
 */
@Slf4j
public class RestTemplateHandler implements ClientHttpRequestInterceptor {

    private final AgileLoggerContext agileLoggerContext;

    public RestTemplateHandler(AgileLoggerContext agileLoggerContext) {
        this.agileLoggerContext = agileLoggerContext;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        if (!agileLoggerContext.getProperties().getRpc().getRestTemplate().isEnable()) {
            return execution.execute(request, body);
        }

        String tag = DurationWatcher.start();
        DurationWatcher.Duration duration = DurationWatcher.get(tag);
        Parent parent = AgileLoggerContext.getParent();

        RequestLog requestLog = new RequestLog(parent.getId(), TransactionUtils.get(), duration.getStartTimeToDate());
        requestLog.setTag(agileLoggerContext.getProperties().getRpc().getRestTemplate().getDefaultTag());

        // Intercept the request
        interceptRequest(request, body, requestLog, parent);

        try {
            // Execute the request
            ClientHttpResponse response = execution.execute(request, body);
            // Proxy
            ClassLoader classLoader = response.getClass().getClassLoader();
            Class<?>[] interfaces = new Class[]{ClientHttpResponse.class};
            InvocationHandler invocationHandler = new ClientHttpResponseHandler(response);
            response = (ClientHttpResponse) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
            // Intercept the response
            return interceptResponse(response, requestLog, tag, parent);
        } catch (Exception ex) {
            requestLog.setLevel(RequestLog.LEVEL_ERROR);
            requestLog.setException(ExceptionUtils.getSimpleMessage(ex, 1024));
            recordLog(requestLog, parent);
            throw new RuntimeException(ex);
        }
    }

    private void interceptRequest(HttpRequest request, byte[] body, RequestLog requestLog, Parent parent) {
        requestLog.setMethod(request.getMethodValue().toUpperCase(Locale.ROOT));

        // Uri & Url
        URI uri = request.getURI();
        requestLog.setUrl(StringUtils.urlDecode(uri.toString()));
        String[] arr = uri.toString().split("//");
        if (arr.length > 1) {
            requestLog.setUri(StringUtils.urlDecode(arr[1].substring(arr[1].indexOf("/"))));
        }

        requestLog.setProtocol(uri.getScheme());
        requestLog.setServerName(uri.getHost());
        requestLog.setRemoteAddr(uri.getHost());
        requestLog.setRemotePort(uri.getPort());
        requestLog.setQuery(StringUtils.urlDecode(uri.getQuery()));
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            requestLog.setIp(hostAddress);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        requestLog.setLocalAddr("localhost");
        requestLog.setLocalPort(agileLoggerContext.getServerPort());
        requestLog.setDomain(StringUtils.urlDecode(uri.toString()));

        // Request body
        requestLog.setRawBody(CollectionUtils.isNotEmpty(body) ? StringUtils.urlDecode(StringUtils.toStr(body)) : null);
        if (requestLog.getRawBody() != null) {
            try {
                requestLog.setBody(JsonUtils.mapper.readValue(requestLog.getRawBody(), Map.class));
            } catch (JsonProcessingException e) {
                LoggerPrinter.warn(log, String.format("Cannot parse body to json: %s", requestLog.getRawBody()));
            }
        }

        // url params
        final HashMap<String, String> params = new LinkedHashMap<>();
        if (requestLog.getUrl() != null && requestLog.getUrl().split("\\?").length > 1) {
            final String[] urlSplitArr = requestLog.getUrl().split("\\?");
            final String[] paramKeyValueArr = urlSplitArr[1].split("&");
            for (String keyValue : paramKeyValueArr) {
                final String[] keyValueArr = keyValue.split("=");
                params.put(keyValueArr[0], keyValueArr.length > 1 ? keyValueArr[1] : null);
            }
        }
        requestLog.setParams(params);

        // Headers
        Map<String, String> headers = new HashMap<>();
        request.getHeaders().forEach((key, values) -> {
            String value = String.join(",", values);
            headers.put(key, StringUtils.urlDecode(value));
        });
        requestLog.setHeaders(headers);


        // Build ExecuteInfo
        ExecuteInfo executeInfo = new ExecuteInfo(parent.getMethod(), parent.getArgs());
        requestLog.setExecuteInfo(executeInfo);
    }

    private ClientHttpResponse interceptResponse(ClientHttpResponse response, RequestLog requestLog, String tag, Parent parent) throws IOException {
        try {
            DurationWatcher.Duration duration = DurationWatcher.stop(tag);
            requestLog.setDuration(duration.getDuration());

            // Response info
            RequestLog.Response logResponse = RequestLog.buildResponse();
            logResponse.setStatus(response.getStatusCode().value());
            Map<String, String> headers = new HashMap<>();
            response.getHeaders().forEach((key, values) -> {
                String value = String.join(",", values);
                headers.put(key, value);
                if ("Content-Type".equalsIgnoreCase(key)) {
                    logResponse.setContentType(value);
                }
            });
            logResponse.setHeaders(headers);
            requestLog.setResponse(logResponse);

            // Result info
            JsonNode result = null;
            InputStream inputStream = response.getBody();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, outputStream);
            byte[] bytes = outputStream.toByteArray();
            String responseBody = StringUtils.toStr(bytes);
            try {
                result = JsonUtils.mapper.readTree(responseBody);
                requestLog.setResult(result);
            } catch (Exception ex) {
                requestLog.setResult(responseBody);
            }

            // Exception & Level
            ResponseSuccessDefineProcessor responseSuccessDefineProcessor = agileLoggerContext.getResponseSuccessDefineProcessor();
            String exception = responseSuccessDefineProcessor.processor(result);
            if (exception != null || response.getStatusCode().value() != R.HttpStatus.OK.getCode()) {
                requestLog.setLevel(RequestLog.LEVEL_ERROR);
                requestLog.setException(exception);
            }
        } finally {
            recordLog(requestLog, parent);
        }
        return response;
    }

    private void recordLog(RequestLog requestLog, Parent parent) {
        // Record Log
        agileLoggerContext.getCurrentRecordProcessor().processor(requestLog);
        // Set Parent
        AgileLoggerContext.setParent(new Parent(requestLog.getLogId(), parent.getMethod(), parent.getArgs()));
    }
}
