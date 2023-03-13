package io.github.thebesteric.framework.agile.logger.spring.processor.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import io.github.thebesteric.framework.agile.logger.commons.utils.CollectionUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.DurationWatcher;
import io.github.thebesteric.framework.agile.logger.commons.utils.JsonUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.core.domain.ExecuteInfo;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.domain.RequestLog;
import io.github.thebesteric.framework.agile.logger.spring.processor.RequestLoggerProcessor;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerContext;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerRequestWrapper;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

/**
 * AbstractRequestLoggerProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractRequestLoggerProcessor implements RequestLoggerProcessor {

    private final AgileLoggerSpringProperties properties;

    @Override
    public RequestLog processor(String id, AgileLoggerRequestWrapper requestWrapper, AgileLoggerResponseWrapper responseWrapper, DurationWatcher.Duration duration) throws IOException {
        RequestLog requestLog = new RequestLog(id, requestWrapper, responseWrapper, duration);
        try {
            if (requestLog.getResult() != null) {
                requestLog.setResult(JsonUtils.mapper.readTree(requestLog.getResult().toString()));
            }
        } catch (Exception ex) {
            LoggerPrinter.error(log, "Cannot parse {} to json", requestLog.getResult());
        }
        Method method = getMethod(requestLog.getUri());
        if (method != null) {
            buildSyntheticAgileLogger(method, requestLog);
            if (responseWrapper.getException() != null) {
                requestLog.setLevel(InvokeLog.LEVEL_ERROR);
            }
            requestLog.setExecuteInfo(new ExecuteInfo(method, null, duration));
        }

        if (properties.isRewrite()) {
            rewriteField(requestLog);
        }

        return doAfterProcessor(requestLog);
    }

    /**
     * Executes when processor is processed
     *
     * @param requestLog {@link RequestLog}
     * @return RequestLog
     */
    public abstract RequestLog doAfterProcessor(RequestLog requestLog);

    private void rewriteField(RequestLog requestLog) {
        Map<Field, Object> rewriteFields = AgileLoggerContext.getRewriteFields();
        if (CollectionUtils.isEmpty(rewriteFields)) {
            return;
        }
        doRewriteField(requestLog.getResult(), rewriteFields);
    }

    public void doRewriteField(Object result, Map<Field, Object> rewriteFields) {
        try {
            if (result instanceof ObjectNode) {
                ObjectNode objectNode = (ObjectNode) result;

                Iterator<String> fieldNames = objectNode.fieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    JsonNode jsonNode = objectNode.get(fieldName);
                    // POJO
                    if (jsonNode instanceof ObjectNode) {
                        doRewriteField(jsonNode, rewriteFields);
                    }
                    // List or Array or Map
                    else if (jsonNode instanceof ArrayNode) {
                        ArrayNode arrayNode = (ArrayNode) jsonNode;
                        for (JsonNode node : arrayNode) {
                            doRewriteField(node, rewriteFields);
                        }
                    }

                    if (jsonNode instanceof ValueNode) {
                        Map.Entry<Field, Object> entry = rewriteFields.entrySet().stream()
                                .filter(e -> e.getKey().getName().equals(fieldName)).findFirst().orElse(null);
                        if (entry != null) {
                            if (jsonNode instanceof IntNode || jsonNode instanceof BigIntegerNode || jsonNode instanceof ShortNode) {
                                objectNode.put(fieldName, (int) entry.getValue());
                            } else if (jsonNode instanceof LongNode) {
                                objectNode.put(fieldName, (long) entry.getValue());
                            } else if (jsonNode instanceof FloatNode) {
                                objectNode.put(fieldName, (float) entry.getValue());
                            } else if (jsonNode instanceof DoubleNode) {
                                objectNode.put(fieldName, (double) entry.getValue());
                            } else if (jsonNode instanceof BooleanNode) {
                                objectNode.put(fieldName, (boolean) entry.getValue());
                            } else {
                                objectNode.put(fieldName, String.valueOf(entry.getValue()));
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
