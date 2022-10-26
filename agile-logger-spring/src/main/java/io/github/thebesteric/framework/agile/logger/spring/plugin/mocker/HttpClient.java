package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker;

import io.github.thebesteric.framework.agile.logger.commons.utils.UrlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * HttpClient
 *
 * @author Eric Joe
 * @since 1.0
 */
public interface HttpClient {

    ResponseEntry execute(String url, Method method, Object[] args) throws Exception;

    /**
     * Merge url parameters
     * <p>
     * url: url?a=1&b=2
     * query: parameters: params{c=3, d=4}
     * return url?a=1&b=2&c=3&d=4
     *
     * @param url    url
     * @param params params
     * @return String
     */
    default String mergeUrlParams(String url, Map<String, Object> params) {
        return UrlUtils.mergeUrlParams(url, params);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class ResponseEntry {
        private int code;
        private String body;
    }

}
