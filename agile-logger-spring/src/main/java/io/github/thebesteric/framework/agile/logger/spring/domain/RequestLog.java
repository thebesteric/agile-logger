package io.github.thebesteric.framework.agile.logger.spring.domain;

import io.github.thebesteric.framework.agile.logger.commons.utils.*;
import io.github.thebesteric.framework.agile.logger.core.annotation.Column;
import io.github.thebesteric.framework.agile.logger.core.annotation.Table;
import io.github.thebesteric.framework.agile.logger.core.domain.AbstractEntity;
import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerRequestWrapper;
import io.github.thebesteric.framework.agile.logger.spring.wrapper.AgileLoggerResponseWrapper;
import lombok.*;

import java.io.IOException;
import java.lang.reflect.Field;
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
@Table(name = "request")
public class RequestLog extends InvokeLog {

    @Column(length = 64, comment = "Session ID")
    private String sessionId;

    @Column(length = 512, comment = "URI")
    private String uri;

    @Column(length = 512, comment = "URL")
    private String url;

    @Column(length = 128, comment = "请求方法")
    private String method;

    @Column(length = 64, comment = "请求协议")
    private String protocol;

    @Column(length = 128, comment = "IP")
    private String ip;

    @Column(length = 128, comment = "域信息")
    private String domain;

    @Column(length = 128, comment = "服务器名")
    private String serverName;

    @Column(length = 64, comment = "本地地址")
    private String localAddr;

    @Column(type = Column.Type.SMALL_INT, unsigned = true, comment = "本地端口")
    private Integer localPort;

    @Column(length = 64, comment = "远程地址")
    private String remoteAddr;

    @Column(type = Column.Type.SMALL_INT, unsigned = true, comment = "远程地址")
    private Integer remotePort;

    @Column(length = 2048, comment = "请求参数")
    private String query;

    @Column(type = Column.Type.JSON, comment = "COOKIES")
    private Set<Cookie> cookies = new HashSet<>();

    @Column(type = Column.Type.JSON, comment = "请求头信息")
    private Map<String, String> headers = new HashMap<>();

    @Column(type = Column.Type.JSON, comment = "请求参数")
    private Map<String, String> params = new HashMap<>();

    @Column(type = Column.Type.JSON, comment = "请求体")
    private Object body;

    @Column(length = 2048, comment = "原生请求体", version = 1)
    private String rawBody;

    @Column(type = Column.Type.INT, comment = "运行时长")
    private Long duration;

    @Column(type = Column.Type.JSON, comment = "响应信息")
    private Response response;

    public RequestLog() {
        super();
    }

    public RequestLog(String logParentId) {
        super(logParentId);
    }

    public RequestLog(String id, AgileLoggerRequestWrapper requestWrapper, AgileLoggerResponseWrapper responseWrapper, DurationWatcher.Duration duration) throws IOException {
        this(requestWrapper, responseWrapper, duration);
        this.logId = id;
    }

    public RequestLog(AgileLoggerRequestWrapper requestWrapper, AgileLoggerResponseWrapper responseWrapper, DurationWatcher.Duration duration) throws IOException {
        this.trackId = TransactionUtils.get();
        this.threadName = Thread.currentThread().getName();
        this.createdAt = new Date(duration.getStartTime());
        this.duration = duration.getDuration();
        this.body = requestWrapper.getBody();
        this.rawBody = requestWrapper.getRawBody();
        this.result = StringUtils.bytesToString(responseWrapper.getByteArray());
        this.serverName = requestWrapper.getServerName();
        this.sessionId = requestWrapper.getRequestedSessionId();
        String queryString = requestWrapper.getQueryString();
        if (queryString != null) {
            this.query = URLDecoder.decode(queryString, StandardCharsets.UTF_8);
        }
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

        // exception message
        int limit = 1024;
        Field field = ReflectUtils.getField(InvokeLog.class, InvokeLog.EXCEPTION_FIELD_NAME);
        if (field != null && field.isAnnotationPresent(Column.class)) {
            limit = field.getAnnotation(Column.class).length();
        }
        this.exception = ExceptionUtils.getSimpleMessage(responseWrapper.getException(), limit);

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

    public static Response buildResponse() {
        return new Response();
    }

}
