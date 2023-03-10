package io.github.thebesteric.framework.agile.logger.plugin.mybatis.processor;

import io.github.thebesteric.framework.agile.logger.core.domain.InvokeLog;
import org.apache.ibatis.plugin.Invocation;

public interface StatementProcessor {
    String PAGE_OFFSET = "offset";
    String PAGE_LIMIT = "limit";

    boolean supports(Invocation invocation);

    InvokeLog processor(Invocation invocation) throws Throwable;
}
