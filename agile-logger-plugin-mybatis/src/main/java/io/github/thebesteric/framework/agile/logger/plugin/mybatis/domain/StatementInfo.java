package io.github.thebesteric.framework.agile.logger.plugin.mybatis.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class StatementInfo {
    private String sql;
    private Map<String, Object> parameters;
}