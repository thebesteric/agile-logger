package io.github.thebesteric.framework.agile.logger.spring.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

/**
 * MetricRequestLog
 *
 * @author Eric Joe
 * @version 1.0
 */
@Getter
@Setter
public class MetricsRequestLog extends RequestLog {

    private Metrics metrics;

    public MetricsRequestLog(RequestLog requestLog, Metrics metrics) {
        BeanUtils.copyProperties(requestLog, this);
        this.metrics = metrics;
    }

    @Getter
    @Setter
    public static class Metrics {

        private long totalRequest = 0L;
        private long avgResponseTime = 0L;
        private long minResponseTime = 0L;
        private long maxResponseTime = 0L;
        @JsonIgnore
        private long totalResponseTime = 0L;
        private String maxResponseTrackId;
        private String minResponseTrackId;

        public synchronized void calc(RequestLog requestLog) {
            Long duration = requestLog.getDuration();
            totalRequest++;
            totalResponseTime += duration;
            avgResponseTime = totalResponseTime / totalRequest;
            if (duration > maxResponseTime) {
                maxResponseTime = duration;
                maxResponseTrackId = requestLog.getTrackId();
            }
            if (duration < minResponseTime || minResponseTime == 0L) {
                minResponseTime = duration;
                minResponseTrackId = requestLog.getTrackId();
            }
        }
    }

}
