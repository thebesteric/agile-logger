package io.github.thebesteric.framework.agile.logger.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.thebesteric.framework.agile.logger.core.AgileContext;
import io.github.thebesteric.framework.agile.logger.core.annotation.Column;
import io.github.thebesteric.framework.agile.logger.core.annotation.Table;
import io.github.thebesteric.framework.agile.logger.core.utils.DefaultIdGenerator;

import java.util.Date;

/**
 * InvokeLog
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-27 12:15:13
 */
@Table(name = "invoke")
public class InvokeLog extends AbstractEntity {

    @Column(length = 64)
    protected String id;

    @Column(length = 64)
    protected String parentId;

    @Column(length = 32)
    protected String tag = TAG_DEFAULT;

    @Column(length = 32)
    protected String level = LEVEL_INFO;

    @Column(length = 64)
    protected String trackId;

    @Column(length = 32)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createdAt = new Date();

    @Column(type = "json")
    protected ExecuteInfo executeInfo;

    @Column(type = "json")
    protected Object result;

    @Column(length = 256)
    protected String exception;

    @Column(type = "json")
    protected Object extra;

    @Column(length = 32)
    protected String threadName = Thread.currentThread().getName();

    public InvokeLog() {
        if (AgileContext.idGenerator == null) {
            AgileContext.idGenerator = DefaultIdGenerator.getInstance();
        }
        this.id = AgileContext.idGenerator.generate();
    }

    public InvokeLog(String parentId) {
        this();
        this.parentId = parentId;
    }

    public static Builder builder(InvokeLog invokeLog) {
        return new Builder(invokeLog);
    }

    public static Builder builder() {
        return new Builder(new InvokeLog());
    }

    public String print() {
        return this.toString();
    }

    public static class Builder {

        private final InvokeLog invokeLog;

        public Builder(InvokeLog invokeLog) {
            this.invokeLog = invokeLog;
        }

        public Builder id(String id) {
            this.invokeLog.id = id;
            return this;
        }

        public Builder parentId(String parentId) {
            this.invokeLog.parentId = parentId;
            return this;
        }

        public Builder tag(String tag) {
            this.invokeLog.tag = tag;
            return this;
        }

        public Builder level(String level) {
            this.invokeLog.level = level;
            return this;
        }

        public Builder trackId(String trackId) {
            this.invokeLog.trackId = trackId;
            return this;
        }

        public Builder createdAt(Date createdAt) {
            this.invokeLog.createdAt = createdAt;
            return this;
        }

        public Builder createdAt(long timestamp) {
            return this.createdAt(new Date(timestamp));
        }

        public Builder executeInfo(ExecuteInfo executeInfo) {
            this.invokeLog.executeInfo = executeInfo;
            return this;
        }

        public Builder result(Object result) {
            this.invokeLog.result = result;
            return this;
        }

        public Builder exception(String exception) {
            this.invokeLog.exception = exception;
            return this;
        }

        public Builder extra(String extra) {
            this.invokeLog.extra = extra;
            return this;
        }

        public Builder threadName(String threadName) {
            this.invokeLog.threadName = threadName;
            return this;
        }

        public InvokeLog build() {
            if (this.invokeLog.createdAt == null) {
                this.invokeLog.createdAt = new Date();
            }
            if (this.invokeLog.threadName == null) {
                this.invokeLog.threadName = Thread.currentThread().getName();
            }
            return this.invokeLog;
        }
    }

    /* getter and setter */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public ExecuteInfo getExecuteInfo() {
        return executeInfo;
    }

    public void setExecuteInfo(ExecuteInfo executeInfo) {
        this.executeInfo = executeInfo;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}
