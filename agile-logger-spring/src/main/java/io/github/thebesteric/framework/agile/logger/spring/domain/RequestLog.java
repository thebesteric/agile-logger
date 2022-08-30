package io.github.thebesteric.framework.agile.logger.spring.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.thebesteric.framework.agile.logger.commons.utils.DurationWatcher;
import io.github.thebesteric.framework.agile.logger.commons.utils.ExceptionUtils;
import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.core.annotation.Column;
import io.github.thebesteric.framework.agile.logger.core.domain.AbstractEntity;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.spring.TransactionUtils;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerRequestWrapper;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerResponseWrapper;
import lombok.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RequestLog
 *
 * @author Eric Joe
 * @since 1.0
 */
@Getter
@Setter
public class RequestLog extends InvokeLog {

    @JsonProperty("session_id")
    @Column(length = 64)
    private String sessionId;

    @Column(length = 512)
    private String uri;

    @Column(length = 512)
    private String url;

    @Column(length = 128)
    private String method;

    @Column(length = 64)
    private String protocol;

    @Column(length = 128)
    private String ip;

    @Column(length = 128)
    private String domain;
    @JsonProperty("server_name")
    @Column(length = 128)
    private String serverName;

    @JsonProperty("local_addr")
    @Column(length = 64)
    private String localAddr;

    @JsonProperty("local_port")
    @Column(length = 11, type = "int")
    private int localPort;

    @JsonProperty("remote_addr")
    @Column(length = 64)
    private String remoteAddr;

    @JsonProperty("remote_port")
    @Column(length = 11, type = "int")
    private int remotePort;

    @JsonProperty("query")
    @Column(length = 2048)
    private String query;

    @Column(type = "json")
    private Set<Cookie> cookies = new HashSet<>();

    @Column(type = "json")
    private Map<String, String> headers = new HashMap<>();

    @Column(type = "json")
    private Map<String, String> params = new HashMap<>();

    @Column(type = "json")
    private Object body;

    @JsonProperty("raw_body")
    @Column(length = 2048)
    private String rawBody;

    @JsonProperty("duration")
    @Column(length = 11, type = "int")
    private long duration;

    @Column(type = "json")
    private Response response;

    public RequestLog(String id, AgileLoggerRequestWrapper requestWrapper, AgileLoggerResponseWrapper responseWrapper, DurationWatcher.Duration duration) throws IOException {
        this(requestWrapper, responseWrapper, duration);
        this.id = id;
    }

    public RequestLog(AgileLoggerRequestWrapper requestWrapper, AgileLoggerResponseWrapper responseWrapper, DurationWatcher.Duration duration) throws IOException {

        this.trackId = TransactionUtils.get();
        this.threadName = Thread.currentThread().getName();
        this.level = responseWrapper.getLevel() == null ? InvokeLog.LEVEL_INFO : responseWrapper.getLevel();
        this.exception = ExceptionUtils.getSimpleMessage(responseWrapper.getException());
        this.createdAt = new Date(duration.getStartTime());
        this.duration = duration.getDuration();
        this.body = requestWrapper.getBody();
        this.rawBody = requestWrapper.getRawBody();
        this.result = StringUtils.bytesToString(responseWrapper.getByteArray());
        this.serverName = requestWrapper.getServerName();
        this.sessionId = requestWrapper.getRequestedSessionId();
        this.query = URLDecoder.decode(requestWrapper.getQueryString(), StandardCharsets.UTF_8);
        this.method = requestWrapper.getMethod();
        this.protocol = requestWrapper.getProtocol();
        this.ip = requestWrapper.getIpAddress();
        this.domain = requestWrapper.getDomain();
        this.localAddr = requestWrapper.getLocalAddr();
        this.localPort = requestWrapper.getLocalPort();
        this.remoteAddr = requestWrapper.getRemoteAddr();
        this.remotePort = requestWrapper.getRemotePort();
        this.url = requestWrapper.getUrlWithQuery();
        this.uri = requestWrapper.getRequestURI();

        // params
        Enumeration<String> parameterNames = requestWrapper.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            String parameterValue = requestWrapper.getParameter(parameterName);
            this.getParams().put(parameterName, parameterValue);
        }

        // headers
        Enumeration<String> headerNames = requestWrapper.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = requestWrapper.getHeader(headerName);
            this.getHeaders().put(headerName, headerValue);
        }

        // cookies
        javax.servlet.http.Cookie[] cookies = requestWrapper.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (javax.servlet.http.Cookie cookie : cookies) {
                this.getCookies().add(new RequestLog.Cookie(cookie));
            }
        }

        // response
        this.response = new Response();
        this.response.setStatus(responseWrapper.getStatus());
        this.response.setContentType(responseWrapper.getContentType());
        this.response.setLocale(responseWrapper.getLocale().toString());
        Map<String, String> responseHeaders = responseWrapper.getHeaderNames().stream().collect(Collectors.toMap((key) -> key, responseWrapper::getHeader));
        this.response.setHeaders(responseHeaders);
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class Cookie extends AbstractEntity {

        private String name;
        private String value;
        private String comment;
        private String domain;
        private int maxAge = -1;
        private String path;
        private boolean secure;
        private int version = 0;
        private boolean isHttpOnly = false;

        public Cookie(javax.servlet.http.Cookie cookie) {
            this.name = cookie.getName();
            this.value = cookie.getValue();
            this.comment = cookie.getComment();
            this.domain = cookie.getDomain();
            this.maxAge = cookie.getMaxAge();
            this.path = cookie.getPath();
            this.secure = cookie.getSecure();
            this.version = cookie.getVersion();
            this.isHttpOnly = cookie.isHttpOnly();
        }

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class Response extends AbstractEntity {
        private int status;
        private String contentType;
        private String locale;
        private Map<String, String> headers = new HashMap<>();
    }

}
