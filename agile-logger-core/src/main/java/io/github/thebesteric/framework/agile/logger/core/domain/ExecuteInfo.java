package io.github.thebesteric.framework.agile.logger.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.thebesteric.framework.agile.logger.commons.utils.DurationWatcher;
import io.github.thebesteric.framework.agile.logger.core.annotation.Column;

import java.lang.reflect.Method;
import java.util.Date;

public class ExecuteInfo extends AbstractEntity {

    private String className;
    private MethodInfo methodInfo;

    @Column(length = 32)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createdAt;

    private long duration;

    public ExecuteInfo() {
        this.createdAt = new Date();
    }

    public ExecuteInfo(Method method, Object[] args) {
        super();
        this.className = method.getDeclaringClass().getName();
        this.methodInfo = new MethodInfo(method, args);
    }

    public ExecuteInfo(Method method, Object[] args, DurationWatcher.Duration duration) {
        this(method, args);
        this.createdAt = new Date(duration.getStartTime());
        this.duration = duration.getDuration();
    }

    /* getter and setter */

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public MethodInfo getMethodInfo() {
        return methodInfo;
    }

    public void setMethodInfo(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}