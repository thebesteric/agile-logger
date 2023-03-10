package io.github.thebesteric.framework.agile.logger.plugin.mybatis.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
public class SQLExecuteInfo implements Serializable {
    private String sqlStatement;
    private Map<String, Object> parameters;
}
