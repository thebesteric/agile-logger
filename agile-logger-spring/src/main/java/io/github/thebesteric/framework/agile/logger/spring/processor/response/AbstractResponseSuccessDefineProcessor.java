package io.github.thebesteric.framework.agile.logger.spring.processor.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.thebesteric.framework.agile.logger.commons.utils.CollectionUtils;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import io.github.thebesteric.framework.agile.logger.spring.processor.ResponseSuccessDefineProcessor;

/**
 * AbstractResponseCodeReturnedProcessor
 * <p>Customize Code and value returned successfully when HttpStatus is 200
 *
 * @author Eric Joe
 * @since 1.0
 */
public abstract class AbstractResponseSuccessDefineProcessor implements ResponseSuccessDefineProcessor {

    protected AgileLoggerSpringProperties.ResponseSuccessDefine responseSuccessDefine;

    public AbstractResponseSuccessDefineProcessor(AgileLoggerSpringProperties.ResponseSuccessDefine responseSuccessDefine) {
        this.responseSuccessDefine = responseSuccessDefine;
    }

    /**
     * process exception message
     * <p>Customize Code and value returned successfully when HttpStatus is 200
     * <p>If no exception occurs, return null
     *
     * @param resultJsonNode JsonNode
     * @param result         Object
     * @return String
     */
    abstract String doProcessor(JsonNode resultJsonNode, Object result);

    @Override
    public AgileLoggerSpringProperties.ResponseSuccessDefine getResponseSuccessDefine() {
        return this.responseSuccessDefine;
    }

    @Override
    public void setResponseSuccessDefine(AgileLoggerSpringProperties.ResponseSuccessDefine responseSuccessDefine) {
        this.responseSuccessDefine = responseSuccessDefine;
    }

    @Override
    public String processor(Object result) throws JsonProcessingException {
        // Result converts to JsonNode
        JsonNode resultJsonNode = getResultJsonNode(result);
        return doProcessor(resultJsonNode, result);
    }

    /**
     * Gets the default response codes and message fields
     *
     * @return {@link AgileLoggerSpringProperties.ResponseSuccessDefine}
     */
    public static AgileLoggerSpringProperties.ResponseSuccessDefine getDefaultResponseSuccessDefine() {
        AgileLoggerSpringProperties.ResponseSuccessDefine responseSuccessDefine = new AgileLoggerSpringProperties.ResponseSuccessDefine();
        responseSuccessDefine.setCodeFields(CollectionUtils.createList(new AgileLoggerSpringProperties.ResponseSuccessDefine.CodeField("code", 200)));
        responseSuccessDefine.setMessageFields(CollectionUtils.createList("message", "msg"));
        return responseSuccessDefine;
    }
}
