package io.github.thebesteric.framework.agile.logger.core.domain;

import java.util.Arrays;

public enum SqlCommandType {
    INSERT, UPDATE, DELETE, SELECT;

    public static SqlCommandType of(String name) {
        return Arrays.stream(SqlCommandType.values()).filter(c-> c.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
