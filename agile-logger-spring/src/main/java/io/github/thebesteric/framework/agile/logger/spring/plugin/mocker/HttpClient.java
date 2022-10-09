package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpClient
 *
 * @author Eric Joe
 * @since 1.0
 */
public interface HttpClient {

    ResponseEntry doGet(String url) throws Exception;

    /**
     * Merge url parameters
     *
     * @param url    url: url?a=1&b=2
     * @param params query parameters: params{c=3, d=4}
     * @return url?a=1&b=2&c=3&d=4
     */
    default String mergeUrlParams(String url, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        if (url.contains("?")) {
            String[] arr = url.split("\\?");
            url = arr[0];
            String[] pairs = arr[1].split("&");
            for (String pair : pairs) {
                String[] urlParam = pair.split("=");
                params.put(urlParam[0], urlParam[1]);
            }
        }
        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> {
            sb.append(k).append("=").append(v).append("&");
        });
        if (sb.indexOf("&") != -1) {
            sb.deleteCharAt(sb.lastIndexOf("&"));
            url += "?" + sb;
        }
        return url;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class ResponseEntry {
        private int code;
        private String body;
    }

}
