package io.github.thebesteric.framework.agile.logger.spring.processor.response;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.thebesteric.framework.agile.logger.commons.utils.ExceptionUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.LoggerPrinter;
import io.github.thebesteric.framework.agile.logger.spring.config.AgileLoggerSpringProperties;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;

/**
 * DefaultResponseSuccessDefineProcessorProcessor
 * <p>Customize Code and value returned successfully when HttpStatus is 200
 *
 * @author Eric Joe
 * @since 1.0
 */
@Slf4j
public class DefaultResponseSuccessDefineProcessorProcessor extends AbstractResponseSuccessDefineProcessor {

    public DefaultResponseSuccessDefineProcessorProcessor() {
        super(DefaultResponseSuccessDefineProcessorProcessor.getDefaultResponseSuccessDefine());
    }

    @Override
    String doProcessor(Method method, JsonNode resultJsonNode, Object result) {
        try {
            // Get code fields
            List<AgileLoggerSpringProperties.ResponseSuccessDefine.CodeField> codeFields = this.responseSuccessDefine.getCodeFields();

            // Check for a match. If it matches, the program is executing normally
            String codeValue = null;
            for (AgileLoggerSpringProperties.ResponseSuccessDefine.CodeField codeField : codeFields) {
                String codeName = codeField.getName();
                JsonNode jsonCodeField = getJsonNodeField(resultJsonNode, codeName);
                // Meet the expected results
                if (jsonCodeField != null) {
                    codeValue = jsonCodeField.asText();
                    if (codeValue.equals(codeField.getValue().toString())) {
                        // Passed successfully
                        return null;
                    }
                }
            }

            // If the code does not match, look for the error message response field
            for (String messageField : this.responseSuccessDefine.getMessageFields()) {
                JsonNode jsonMessageField = getJsonNodeField(resultJsonNode, messageField);
                // Return the error messages
                if (jsonMessageField != null) {
                    return jsonMessageField.asText();
                }
            }

            // If code has a value, it indicates an exception, otherwise it indicates normal execution
            return codeValue;


        } catch (Exception ex) {
            LoggerPrinter.debug(log, ex.getMessage());
            return ExceptionUtils.getSimpleMessage(ex);
        }
    }
}
