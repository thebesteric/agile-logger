package io.github.thebesteric.framework.agile.logger.plugin.mybatis.processor.impl;

import io.github.thebesteric.framework.agile.logger.plugin.mybatis.processor.AbstractStatementProcessor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;

public class DeleteStatementProcessor extends AbstractStatementProcessor {
    @Override
    public boolean supports(Invocation invocation) {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        return SqlCommandType.DELETE == mappedStatement.getSqlCommandType();
    }
}