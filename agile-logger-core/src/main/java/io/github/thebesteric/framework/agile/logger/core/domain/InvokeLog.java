package io.github.thebesteric.framework.agile.logger.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
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

    @Column(length = 64, unique = true, comment = "日志 ID")
    protected String logId;

    @Column(length = 64, comment = "父日志 ID")
    protected String logParentId;

    @Column(length = 32, comment = "标签", nullable = false)
    protected String tag = TAG_DEFAULT;

    @Column(length = 32, comment = "日志级别")
    protected String level = LEVEL_INFO;

    @Column(length = 64, comment = "日志链追踪 ID")
    protected String trackId;

    @Column(type = Column.Type.DATETIME, comment = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createdAt = new Date();

    @Column(type = Column.Type.JSON, comment = "执行信息")
    protected ExecuteInfo executeInfo;

    @Column(type = Column.Type.JSON, comment = "执行结果")
    protected Object result;

    @Column(name = EXCEPTION_FIELD_NAME, length = 1024, comment = "异常信息")
    protected String exception;

    @Column(type = Column.Type.JSON, comment = "扩展信息")
    protected Object extra;

    @Column(length = 64, comment = "执行线程名称")
    protected String threadName = Thread.currentThread().getName();

    public InvokeLog() {
        if (AgileContext.idGenerator == null) {
            AgileContext.idGenerator = DefaultIdGenerator.getInstance();
        }
        this.logId = AgileContext.idGenerator.generate();
    }

    public InvokeLog(String logParentId) {
        this();
        this.logParentId = logParentId;
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
            this.invokeLog.logId = id;
            return this;
        }

        public Builder parentId(String parentId) {
            this.invokeLog.logParentId = parentId;
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
            this.invokeLog.extra = StringUtils.blankToNull(extra);
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

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getLogParentId() {
        return logParentId;
    }

    public void setLogParentId(String logParentId) {
        this.logParentId = logParentId;
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
