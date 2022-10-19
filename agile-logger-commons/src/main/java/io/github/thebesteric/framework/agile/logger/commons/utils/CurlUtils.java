package io.github.thebesteric.framework.agile.logger.commons.utils;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.*;

/**
 * CurlUtils
 *
 * @author Eric Joe
 * @version 1.0
 */
public class CurlUtils {

    private static final String FORMAT_HEADER = "-H \"%1$s:%2$s\"";
    private static final String FORMAT_METHOD = "-X %1$s";
    private static final String FORMAT_BODY = "-d '%1$s'";
    private static final String FORMAT_URL = "\"%1$s\"";
    private static final String CONTENT_TYPE = "Content-Type";

    public static String getCurl(HttpServletRequest request) {
        String curl;
        try {
            List<String> parts = new ArrayList<>();
            parts.add("curl");
            String url = request.getRequestURL().toString();
            String method = request.getMethod();
            String contentType = request.getContentType();
            String queryString = request.getQueryString();
            Map<String, String[]> parameterMap = request.getParameterMap();
            parts.add(String.format(FORMAT_METHOD, method.toUpperCase()));

            Map<String, String> headers = new HashMap<>(16);
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                headers.put(key, request.getHeader(key).replaceAll("\"",""));
            }
            headers.forEach((k, v) -> parts.add(String.format(FORMAT_HEADER, k, v)));
            if (StringUtils.isNotEmpty(contentType) && !headers.containsKey(CONTENT_TYPE)) {
                parts.add(String.format(FORMAT_HEADER, CONTENT_TYPE, contentType));
            }
            if (StringUtils.isNotEmpty(queryString)) {
                url += "?" + queryString;
            }
            if (StringUtils.startWith(contentType, ContentType.FORM_URLENCODED.toString()) && CollectionUtils.isNotEmpty(parameterMap)) {
                parameterMap.forEach((k, v) ->
                        parts.add(StringUtils.format("--data-urlencode '{}={}'", k, Array.get(v, 0))));
            }
            if (StringUtils.startWith(contentType, ContentType.JSON.toString())) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                IOUtils.copy(request.getInputStream(), outputStream);
                String body = StringUtils.toStr(outputStream.toByteArray());
                if (StringUtils.isNotEmpty(body)) {
                    parts.add(String.format(FORMAT_BODY, body));
                }
            }
            parts.add(String.format(FORMAT_URL, url));
            curl = String.join(" ", parts);
        } catch (Exception e) {
            e.printStackTrace();
            curl = null;
        }
        return curl;
    }

    public enum ContentType {
        FORM_URLENCODED("application/x-www-form-urlencoded"),
        MULTIPART("multipart/form-data"),
        JSON("application/json"),
        XML("application/xml"),
        TEXT_PLAIN("text/plain"),
        TEXT_XML("text/xml"),
        TEXT_HTML("text/html"),
        OCTET_STREAM("application/octet-stream");

        private final String value;

        ContentType(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return this.getValue();
        }
    }
}
