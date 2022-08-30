package io.github.thebesteric.framework.agile.logger.core.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.thebesteric.framework.agile.logger.commons.utils.JsonUtils;

import java.io.Serializable;

/**
 * AbstractEntity
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-27 12:15:42
 */
public abstract class AbstractEntity implements Serializable {

    public static final String TAG_DEFAULT = "default";

    public static final String LEVEL_DEBUG = "DEBUG";
    public static final String LEVEL_INFO = "INFO";
    public static final String LEVEL_WARN = "WARN";
    public static final String LEVEL_ERROR = "ERROR";

    @Override
    public String toString() {
        try {
            return JsonUtils.mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
