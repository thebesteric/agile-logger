package io.github.thebesteric.framework.agile.logger.rpc.feign.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import feign.*;
import io.github.thebesteric.framework.agile.logger.commons.utils.*;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;
import io.github.thebesteric.framework.agile.logger.core.annotation.IgnoreMethod;
import io.github.thebesteric.framework.agile.logger.core.annotation.IgnoreMethods;
import io.github.thebesteric.framework.agile.logger.core.domain.AbstractEntity;
import io.github.thebesteric.framework.agile.logger.core.domain.ExecuteInfo;
import io.github.thebesteric.framework.agile.logger.core.domain.MethodInfo;
import io.github.thebesteric.framework.agile.logger.core.domain.SyntheticAgileLogger;
import io.github.thebesteric.framework.agile.logger.rpc.feign.domain.LogWrapper;
import io.github.thebesteric.framework.agile.logger.spring.domain.Parent;
import io.github.thebesteric.framework.agile.logger.spring.domain.R;
import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;
import io.github.thebesteric.framework.agile.logger.spring.domain.SpringSyntheticAgileLogger;
import io.github.thebesteric.framework.agile.logger.spring.processor.ResponseSuccessDefineProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.Inet4Address;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * FeignLogger
 *
 * @author Eric Joe
 * @version 1.0
 */
@Slf4j
public class FeignLHandler extends feign.Logger {

    private final static ThreadLocal<LogWrapper> logWrapperThreadLocal = new ThreadLocal<>();

    private final AgileLoggerContext agileLoggerContext;

    public FeignLHandler(AgileLoggerContext agileLoggerContext) {
        this.agileLoggerContext = agileLoggerContext;
    }

    @Override
    public void log(String configKey, String format, Object... args) {
        LoggerPrinter.info(log, String.format(methodTag(configKey) + format, args));
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        RequestTemplate requestTemplate = request.requestTemplate();

        if (!agileLoggerContext.getProperties().getRpc().getFeign().isEnable() || requestTemplate == null) {
            super.logRequest(configKey, logLevel, request);
            return;
        }

        long timestamp = System.currentTimeMillis();
        Parent parent = AgileLoggerContext.getParent();
        RequestLog requestLog = new RequestLog(parent);
        requestLog.setCreatedAt(new Date(timestamp));
        requestLog.setMethod(request.httpMethod().name().toUpperCase(Locale.ROOT));
        requestLog.setTrackId(TransactionUtils.get());

        LogWrapper logWrapper = new LogWrapper(requestLog, parent);

        // Uri & Url
        String url = requestTemplate.url();
        requestLog.setUrl(UrlUtils.urlDecode(url));
        String[] arr = url.split("//");
        if (arr.length > 1) {
            String uri = UrlUtils.urlDecode(arr[1].substring(arr[1].indexOf("/")));
            requestLog.setUri(uri);
        }

        // Uri info
        URI uri = URI.create(url);
        requestLog.setProtocol(uri.getScheme());
        requestLog.setServerName(uri.getHost());
        requestLog.setRemoteAddr(uri.getHost());
        requestLog.setRemotePort(uri.getPort());
        requestLog.setQuery(UrlUtils.urlDecode(uri.getQuery()));
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            requestLog.setIp(hostAddress);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        requestLog.setLocalAddr("localhost");
        requestLog.setLocalPort(agileLoggerContext.getServerPort());
        requestLog.setDomain(UrlUtils.urlDecode(requestTemplate.feignTarget().url()));

        // Request body
        requestLog.setRawBody(CollectionUtils.isNotEmpty(request.body()) ? UrlUtils.urlDecode(StringUtils.toStr(request.body())) : null);
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
        requestTemplate.headers().forEach((key, values) -> {
            String value = String.join(",", values);
            headers.put(key, UrlUtils.urlDecode(value));
        });
        requestLog.setHeaders(headers);

        // Build ExecuteInfo
        ExecuteInfo executeInfo = new ExecuteInfo();
        executeInfo.setCreatedAt(requestLog.getCreatedAt());

        // Class info
        Target<?> target = requestTemplate.feignTarget();
        if (target != null && target.type() != null) {
            Class<?> clazz = target.type();
            executeInfo.setClassName(clazz.getName());
        }

        // Method info
        MethodMetadata methodMetadata = requestTemplate.methodMetadata();
        if (methodMetadata != null && methodMetadata.returnType() != null) {
            MethodInfo methodInfo = new MethodInfo();
            Method method = methodMetadata.method();
            methodInfo.setMethodName(method.getName());
            methodInfo.setReturnType(method.getReturnType().getName());

            // Build SyntheticAgileLogger
            SyntheticAgileLogger syntheticAgileLogger = SpringSyntheticAgileLogger.getSpringSyntheticAgileLogger(method);
            if (StringUtils.equals(AbstractEntity.TAG_DEFAULT, syntheticAgileLogger.getTag())) {
                requestLog.setTag(agileLoggerContext.getProperties().getRpc().getFeign().getDefaultTag());
            } else {
                requestLog.setTag(syntheticAgileLogger.getTag());
            }
            requestLog.setLevel(syntheticAgileLogger.getLevel());
            requestLog.setExtra(syntheticAgileLogger.getExtra());

            // Method signatures
            final LinkedHashMap<String, Object> methodSignatures = new LinkedHashMap<>();
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                methodSignatures.put(parameter.getName(), parameter.getParameterizedType().getTypeName());
            }
            methodInfo.setSignatures(methodSignatures);

            // Method arguments
            final LinkedHashMap<String, Object> methodArgs = new LinkedHashMap<>();
            if (methodMetadata.indexToName() != null && methodMetadata.indexToName().size() > 0) {
                for (Map.Entry<Integer, Collection<String>> entry : methodMetadata.indexToName().entrySet()) {
                    final String paramName = entry.getValue().toArray()[0].toString();
                    final Collection<String> paramValues = request.headers().getOrDefault(paramName, null);
                    String paramValue = null;
                    if (paramValues != null && paramValues.size() > 0) {
                        paramValue = paramValues.toArray()[0].toString();
                    }
                    if (paramValue == null && params.containsKey(paramName)) {
                        paramValue = params.get(paramName);
                    }
                    methodArgs.put(paramName, paramValue);
                }
                methodInfo.setArguments(methodArgs);
            }

            // set MethodInfo
            executeInfo.setMethodInfo(methodInfo);

            // Set Method And Args
            logWrapper.setMethod(method);
            logWrapper.setArgs(methodArgs.values().toArray());
        }

        // Set ExecuteInfo
        requestLog.setExecuteInfo(executeInfo);

        // Set LogWrapper
        logWrapperThreadLocal.set(logWrapper);
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        final LogWrapper logWrapper = logWrapperThreadLocal.get();
        try {
            final RequestLog requestLog = logWrapper.getRequestLog();
            requestLog.setDuration(elapsedTime);

            // Response info
            RequestLog.Response logResponse = RequestLog.buildResponse();
            logResponse.setStatus(response.status());
            Map<String, String> headers = new HashMap<>();
            response.headers().forEach((key, values) -> {
                String value = String.join(",", values);
                headers.put(key, value);
                if ("Content-Type".equalsIgnoreCase(key)) {
                    logResponse.setContentType(value);
                }
            });
            logResponse.setHeaders(headers);
            requestLog.setResponse(logResponse);

            // Result info
            byte[] bodyData = null;
            JsonNode result = null;
            if (response.body() != null) {
                bodyData = feign.Util.toByteArray(response.body().asInputStream());
                if (bodyData.length > 0) {
                    String responseBody = feign.Util.decodeOrDefault(bodyData, feign.Util.UTF_8, "Binary data");
                    try {
                        result = JsonUtils.mapper.readTree(responseBody);
                        requestLog.setResult(result);
                    } catch (Exception ex) {
                        requestLog.setResult(responseBody);
                    }
                }
            }

            // Exception & Level
            ResponseSuccessDefineProcessor responseSuccessDefineProcessor = agileLoggerContext.getResponseSuccessDefineProcessor();
            String exception = responseSuccessDefineProcessor.processor(result);
            if (exception != null || response.status() != R.HttpStatus.OK.getCode()) {
                requestLog.setLevel(RequestLog.LEVEL_ERROR);
                requestLog.setException(exception);
            }

            return response.toBuilder().body(bodyData).build();

        } finally {
            recordLog(logWrapper);
        }
    }

    @Override
    protected IOException logIOException(String configKey, Level logLevel, IOException ioe, long elapsedTime) {
        final LogWrapper logWrapper = logWrapperThreadLocal.get();
        try {
            final RequestLog requestLog = logWrapper.getRequestLog();
            requestLog.setLevel(RequestLog.LEVEL_ERROR);
            requestLog.setException(ExceptionUtils.getSimpleMessage(ioe, 1024));
            requestLog.setDuration(elapsedTime);
        } finally {
            recordLog(logWrapper);
        }
        return ioe;
    }

    private void recordLog(final LogWrapper logWrapper) {
        final RequestLog requestLog = logWrapper.getRequestLog();
        Parent parent = logWrapper.getParent();
        Method method = logWrapper.getMethod();

        // Ignore Method
        if (ignoreMethod(method)) {
            return;
        }

        // Handler RequestLog Attributes
        handleRequestLogAttrs(requestLog, method);

        // Record Log
        agileLoggerContext.getCurrentRecordProcessor().processor(requestLog);
        // Remove ThreadLocal
        logWrapperThreadLocal.remove();
        // Set Parent
        if (parent == null) {
            parent = new Parent(requestLog.getLogId(), method, logWrapper.getArgs());
        } else {
            parent = new Parent(requestLog.getLogId(), parent.getMethod(), parent.getArgs());
        }
        AgileLoggerContext.setParent(parent);
    }

    /**
     * Ignore Method
     *
     * @param method method
     * @author wangweijun
     * @since 2023/7/9 18:47
     */
    private boolean ignoreMethod(Method method) {
        assert method != null;

        // @IgnoreMethods on Type
        Class<?> clazz = method.getDeclaringClass();
        IgnoreMethods ignoreMethods = clazz.getAnnotation(IgnoreMethods.class);
        if (ignoreMethods != null) {
            String[] ignoreMethodsOnType = ignoreMethods.value();
            for (String ignoreMethod : ignoreMethodsOnType) {
                if (Pattern.matches(ignoreMethod, method.getName())) {
                    return true;
                }
            }
        }

        // Handle @AgileLogger On Type
        AgileLogger AgileLoggerOnType = clazz.getAnnotation(AgileLogger.class);
        if (AgileLoggerOnType != null) {
            String[] ignoredMethodsOnType = AgileLoggerOnType.ignoreMethods();
            for (String ignoreMethod : ignoredMethodsOnType) {
                if (Pattern.matches(ignoreMethod, method.getName())) {
                    return true;
                }
            }
        }

        // @IgnoreMethod on Method
        if (method.isAnnotationPresent(IgnoreMethod.class)) {
            return true;
        }

        // Handle @AgileLogger On Method
        AgileLogger agileLoggerOnMethod = method.getAnnotation(AgileLogger.class);
        if (agileLoggerOnMethod != null) {
            String[] ignoredMethodsOnMethod = agileLoggerOnMethod.ignoreMethods();
            for (String ignoreMethod : ignoredMethodsOnMethod) {
                if (Pattern.matches(ignoreMethod, method.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Handler RequestLog Attributes
     *
     * @param requestLog requestLog
     * @param method     method
     * @author wangweijun
     * @since 2023/7/9 18:47
     */
    private void handleRequestLogAttrs(RequestLog requestLog, Method method) {
        // Handle @AgileLogger
        AgileLogger agileLogger = method.getAnnotation(AgileLogger.class);
        if (agileLogger == null) {
            return;
        }

        // Handle tag
        String tagOnAgileLogger = agileLogger.tag();
        if (StringUtils.isNotEmpty(tagOnAgileLogger)) {
            requestLog.setTag(tagOnAgileLogger);
        }

        // Handle extra
        String extra = agileLogger.extra();
        if (StringUtils.isNotEmpty(extra)) {
            requestLog.setExtra(extra);
        }
    }
}
