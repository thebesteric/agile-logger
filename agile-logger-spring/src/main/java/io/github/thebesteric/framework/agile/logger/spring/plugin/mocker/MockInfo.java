package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker;

import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import lombok.Getter;
import lombok.Setter;

/**
 * MockInfo
 *
 * @author Eric Joe
 * @version 1.0
 */
@Getter
@Setter
public class MockInfo {
    private String id;
    private String parentId;

    public MockInfo(InvokeLog invokeLog) {
        this.id = invokeLog.getLogId();
        this.parentId = invokeLog.getLogParentId();
    }
}
