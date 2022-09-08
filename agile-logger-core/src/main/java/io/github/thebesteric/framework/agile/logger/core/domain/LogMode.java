package io.github.thebesteric.framework.agile.logger.core.domain;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * log mode
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022/8/11
 */
public enum LogMode {

    STDOUT("stdout"), LOG("log"), REDIS("redis"), DATABASE("database");

    private final String name;

    LogMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static LogMode getLogMode(String name) {
        LogMode logMode = Arrays.stream(LogMode.values())
                .filter((lm) -> lm.name.equals(name))
                .collect(Collectors.collectingAndThen(Collectors.toList(), (list) -> list.size() == 1 ? list.get(0) : null));
        return logMode == null ? STDOUT : logMode;
    }
}
