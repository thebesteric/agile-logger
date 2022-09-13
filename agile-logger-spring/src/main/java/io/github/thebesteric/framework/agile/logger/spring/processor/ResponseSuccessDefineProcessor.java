package io.github.thebesteric.framework.agile.logger.spring.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.thebesteric.framework.agile.logger.commons.utils.JsonUtils;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;

import java.lang.reflect.Method;

/**
 * ResponseSuccessDefineProcessor
 * <p>Customize Code and value returned successfully when HttpStatus is 200
 *
 * @author Eric Joe
 * @version 1.0
 */
public interface ResponseSuccessDefineProcessor {

    /**
     * process exception message
     * <p>Customize Code and value returned successfully when HttpStatus is 200
     * <p>If no exception occurs, return null
     *
     * @param method method
     * @param result result
     * @return exception message
     */
    String processor(Method method, Object result) throws JsonProcessingException;


    AgileLoggerSpringProperties.ResponseSuccessDefine getResponseSuccessDefine();

    void setResponseSuccessDefine(AgileLoggerSpringProperties.ResponseSuccessDefine responseSuccessDefine);

    /**
     * Get JsonNode for result
     *
     * @param result Object
     * @return JsonNode
     * @throws JsonProcessingException exception
     */
    default JsonNode getResultJsonNode(Object result) throws JsonProcessingException {
        if (result == null) return null;
        String resultJsonStr = JsonUtils.mapper.writeValueAsString(result);
        return JsonUtils.mapper.readTree(resultJsonStr);
    }

    /**
     * Gets the specified fields from JsonNode
     *
     * @param resultJsonNode  JsonNode
     * @param fieldExpression String
     * @return JsonNode
     */
    default JsonNode getJsonNodeField(JsonNode resultJsonNode, String fieldExpression) {
        String[] fields = fieldExpression.split("\\.");
        JsonNode jsonCodeField = null;
        for (String field : fields) {
            jsonCodeField = resultJsonNode.get(field);
            if (jsonCodeField != null) {
                break;
            }
        }
        return jsonCodeField;
    }
}
