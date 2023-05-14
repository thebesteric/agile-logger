package io.github.thebesteric.framework.agile.logger.spring.domain;

import io.github.thebesteric.framework.agile.logger.commons.utils.TransactionUtils;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * Common Response
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022/7/22
 */
@Data
public class R<T> implements Serializable {
    private Integer code;
    private T data;
    private Long timestamp;
    private String message;
    private String trackId;
    private static final String ZONE_OFFSET = "+8";

    private R() {
        super();
    }

    public synchronized static <T> R<T> newInstance() {
        return initInstance(null, null, null, null);
    }

    private synchronized static <T> R<T> initInstance(Integer code, String message, String trackId, T data) {
        R<T> instance = new R<>();
        instance.code = code;
        instance.message = message;
        instance.data = data;
        instance.trackId = trackId;
        instance.timestamp = LocalDateTime.now().toInstant(ZoneOffset.of(ZONE_OFFSET)).toEpochMilli();
        return instance;
    }

    public boolean checkCode(int code) {
        return this.code == code;
    }

    public R setTrackId(String trackId) {
        this.trackId = trackId;
        return this;
    }

    @SuppressWarnings({"unchecked"})
    public R<T> put(String key, Object value) {
        if (data == null) {
            this.data = (T) new HashMap<>();
        }
        if (this.data instanceof Map) {
            ((Map<String, Object>) this.data).put(key, value);
            return this;
        }
        String className = this.data.getClass().getName();
        throw new IllegalArgumentException(String.format("data: [%s] is not a map structure", className));
    }

    public R<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public R<T> setCode(HttpStatus httpStatus) {
        return setCode(httpStatus.code);
    }

    public R<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public R<T> setData(T data) {
        this.data = data;
        return this;
    }

    public static <T> R<T> success(int code, String message, String trackId, T data) {
        return initInstance(code, message, trackId, data);
    }

    public static <T> R<T> success() {
        return success(HttpStatus.OK.memo, null);
    }

    public static <T> R<T> success(T data) {
        return success(HttpStatus.OK.memo, data);
    }

    public static <T> R<T> success(String message, T data) {
        return success(HttpStatus.OK, message, data);
    }

    public static <T> R<T> success(HttpStatus httpStatus, T data) {
        return success(httpStatus, httpStatus.memo, data);
    }

    public static <T> R<T> success(HttpStatus httpStatus, String message, T data) {
        return success(httpStatus.code, null != message ? message : httpStatus.memo, data);
    }

    public static <T> R<T> success(int code, String message, T data) {
        return success(code, message, TransactionUtils.get(), data);
    }

    public static <T> R<T> error(int code, String message, String trackId, T data) {
        return initInstance(code, message, trackId, data);
    }

    public static <T> R<T> error() {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    public static <T> R<T> error(T data) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, data);
    }

    public static <T> R<T> error(String message, T data) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, message, data);
    }

    public static <T> R<T> error(HttpStatus httpStatus, T data) {
        return error(httpStatus, httpStatus.memo, data);
    }

    public static <T> R<T> error(HttpStatus httpStatus, String message, T data) {
        return error(httpStatus.code, null != message ? message : httpStatus.memo, data);
    }

    public static <T> R<T> error(int code, String message, T data) {
        return error(code, message, TransactionUtils.get(), data);
    }

    /**
     * HTTP Response 状态码
     */
    public enum HttpStatus {

        CONTINUE(100, HttpStatus.Series.INFORMATIONAL, "Continue"),
        SWITCHING_PROTOCOLS(101, HttpStatus.Series.INFORMATIONAL, "Switching Protocols"),
        PROCESSING(102, HttpStatus.Series.INFORMATIONAL, "Processing"),
        CHECKPOINT(103, HttpStatus.Series.INFORMATIONAL, "Checkpoint"),
        OK(200, HttpStatus.Series.SUCCESSFUL, "OK"),
        CREATED(201, HttpStatus.Series.SUCCESSFUL, "Created"),
        ACCEPTED(202, HttpStatus.Series.SUCCESSFUL, "Accepted"),
        NON_AUTHORITATIVE_INFORMATION(203, HttpStatus.Series.SUCCESSFUL, "Non-Authoritative Information"),
        NO_CONTENT(204, HttpStatus.Series.SUCCESSFUL, "No Content"),
        RESET_CONTENT(205, HttpStatus.Series.SUCCESSFUL, "Reset Content"),
        PARTIAL_CONTENT(206, HttpStatus.Series.SUCCESSFUL, "Partial Content"),
        MULTI_STATUS(207, HttpStatus.Series.SUCCESSFUL, "Multi-Status"),
        ALREADY_REPORTED(208, HttpStatus.Series.SUCCESSFUL, "Already Reported"),
        IM_USED(226, HttpStatus.Series.SUCCESSFUL, "IM Used"),
        MULTIPLE_CHOICES(300, HttpStatus.Series.REDIRECTION, "Multiple Choices"),
        MOVED_PERMANENTLY(301, HttpStatus.Series.REDIRECTION, "Moved Permanently"),
        FOUND(302, HttpStatus.Series.REDIRECTION, "Found"),
        SEE_OTHER(303, HttpStatus.Series.REDIRECTION, "See Other"),
        NOT_MODIFIED(304, HttpStatus.Series.REDIRECTION, "Not Modified"),
        USE_PROXY(305, HttpStatus.Series.REDIRECTION, "Use Proxy"),
        TEMPORARY_REDIRECT(307, HttpStatus.Series.REDIRECTION, "Temporary Redirect"),
        PERMANENT_REDIRECT(308, HttpStatus.Series.REDIRECTION, "Permanent Redirect"),
        BAD_REQUEST(400, HttpStatus.Series.CLIENT_ERROR, "Bad Request"),
        UNAUTHORIZED(401, HttpStatus.Series.CLIENT_ERROR, "Unauthorized"),
        PAYMENT_REQUIRED(402, HttpStatus.Series.CLIENT_ERROR, "Payment Required"),
        FORBIDDEN(403, HttpStatus.Series.CLIENT_ERROR, "Forbidden"),
        NOT_FOUND(404, HttpStatus.Series.CLIENT_ERROR, "Not Found"),
        METHOD_NOT_ALLOWED(405, HttpStatus.Series.CLIENT_ERROR, "Method Not Allowed"),
        NOT_ACCEPTABLE(406, HttpStatus.Series.CLIENT_ERROR, "Not Acceptable"),
        PROXY_AUTHENTICATION_REQUIRED(407, HttpStatus.Series.CLIENT_ERROR, "Proxy Authentication Required"),
        REQUEST_TIMEOUT(408, HttpStatus.Series.CLIENT_ERROR, "Request Timeout"),
        CONFLICT(409, HttpStatus.Series.CLIENT_ERROR, "Conflict"),
        GONE(410, HttpStatus.Series.CLIENT_ERROR, "Gone"),
        LENGTH_REQUIRED(411, HttpStatus.Series.CLIENT_ERROR, "Length Required"),
        PRECONDITION_FAILED(412, HttpStatus.Series.CLIENT_ERROR, "Precondition Failed"),
        PAYLOAD_TOO_LARGE(413, HttpStatus.Series.CLIENT_ERROR, "Payload Too Large"),
        URI_TOO_LONG(414, HttpStatus.Series.CLIENT_ERROR, "URI Too Long"),
        UNSUPPORTED_MEDIA_TYPE(415, HttpStatus.Series.CLIENT_ERROR, "Unsupported Media Type"),
        REQUESTED_RANGE_NOT_SATISFIABLE(416, HttpStatus.Series.CLIENT_ERROR, "Requested range not satisfiable"),
        EXPECTATION_FAILED(417, HttpStatus.Series.CLIENT_ERROR, "Expectation Failed"),
        I_AM_A_TEAPOT(418, HttpStatus.Series.CLIENT_ERROR, "I'm a teapot"),
        INSUFFICIENT_SPACE_ON_RESOURCE(419, HttpStatus.Series.CLIENT_ERROR, "Insufficient Space On Resource"),
        METHOD_FAILURE(420, HttpStatus.Series.CLIENT_ERROR, "Method Failure"),
        DESTINATION_LOCKED(421, HttpStatus.Series.CLIENT_ERROR, "Destination Locked"),
        UNPROCESSABLE_ENTITY(422, HttpStatus.Series.CLIENT_ERROR, "Unprocessable Entity"),
        LOCKED(423, HttpStatus.Series.CLIENT_ERROR, "Locked"),
        FAILED_DEPENDENCY(424, HttpStatus.Series.CLIENT_ERROR, "Failed Dependency"),
        TOO_EARLY(425, HttpStatus.Series.CLIENT_ERROR, "Too Early"),
        UPGRADE_REQUIRED(426, HttpStatus.Series.CLIENT_ERROR, "Upgrade Required"),
        PRECONDITION_REQUIRED(428, HttpStatus.Series.CLIENT_ERROR, "Precondition Required"),
        TOO_MANY_REQUESTS(429, HttpStatus.Series.CLIENT_ERROR, "Too Many Requests"),
        REQUEST_HEADER_FIELDS_TOO_LARGE(431, HttpStatus.Series.CLIENT_ERROR, "Request Header Fields Too Large"),
        UNAVAILABLE_FOR_LEGAL_REASONS(451, HttpStatus.Series.CLIENT_ERROR, "Unavailable For Legal Reasons"),
        INTERNAL_SERVER_ERROR(500, HttpStatus.Series.SERVER_ERROR, "Internal Server Error"),
        NOT_IMPLEMENTED(501, HttpStatus.Series.SERVER_ERROR, "Not Implemented"),
        BAD_GATEWAY(502, HttpStatus.Series.SERVER_ERROR, "Bad Gateway"),
        SERVICE_UNAVAILABLE(503, HttpStatus.Series.SERVER_ERROR, "Service Unavailable"),
        GATEWAY_TIMEOUT(504, HttpStatus.Series.SERVER_ERROR, "Gateway Timeout"),
        HTTP_VERSION_NOT_SUPPORTED(505, HttpStatus.Series.SERVER_ERROR, "HTTP Version not supported"),
        VARIANT_ALSO_NEGOTIATES(506, HttpStatus.Series.SERVER_ERROR, "Variant Also Negotiates"),
        INSUFFICIENT_STORAGE(507, HttpStatus.Series.SERVER_ERROR, "Insufficient Storage"),
        LOOP_DETECTED(508, HttpStatus.Series.SERVER_ERROR, "Loop Detected"),
        BANDWIDTH_LIMIT_EXCEEDED(509, HttpStatus.Series.SERVER_ERROR, "Bandwidth Limit Exceeded"),
        NOT_EXTENDED(510, HttpStatus.Series.SERVER_ERROR, "Not Extended"),
        NETWORK_AUTHENTICATION_REQUIRED(511, HttpStatus.Series.SERVER_ERROR, "Network Authentication Required");

        final int code;
        final Series series;
        final String memo;

        HttpStatus(int code, Series series, String memo) {
            this.code = code;
            this.series = series;
            this.memo = memo;
        }

        public int getCode() {
            return this.code;
        }

        public Series getSeries() {
            return this.series;
        }

        public String getMemo() {
            return this.memo;
        }

        public enum Series {
            INFORMATIONAL(1),
            SUCCESSFUL(2),
            REDIRECTION(3),
            CLIENT_ERROR(4),
            SERVER_ERROR(5);

            private final int value;

            private Series(int value) {
                this.value = value;
            }

            public int value() {
                return this.value;
            }

            public static HttpStatus.Series valueOf(int statusCode) {
                HttpStatus.Series series = resolve(statusCode);
                if (series == null) {
                    throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
                } else {
                    return series;
                }
            }

            @Nullable
            public static HttpStatus.Series resolve(int statusCode) {
                int seriesCode = statusCode / 100;
                HttpStatus.Series[] values = values();
                for (Series series : values) {
                    if (series.value == seriesCode) {
                        return series;
                    }
                }
                return null;
            }
        }
    }
}
