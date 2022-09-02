package io.github.thebesteric.framework.agile.logger.spring.config;

import io.github.thebesteric.framework.agile.logger.commons.AgileLoggerConstant;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;

/**
 * AgileLoggerConditionMarker
 *
 * @author Eric Joe
 * @version 1.0
 */
public interface AgileLoggerConditionMarker extends Condition {

    default String getProperty(String key, ConditionContext context) {
        return getEnvironment(context).getProperty(key);
    }

    default String getAgileProperty(String key, ConditionContext context) {
        return getProperty(AgileLoggerConstant.PROPERTIES_PREFIX + "." + key, context);
    }

    default Environment getEnvironment(ConditionContext context) {
        return context.getEnvironment();
    }
}
