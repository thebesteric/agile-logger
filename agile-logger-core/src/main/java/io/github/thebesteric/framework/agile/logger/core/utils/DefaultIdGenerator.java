package io.github.thebesteric.framework.agile.logger.core.utils;

import java.util.Locale;
import java.util.UUID;

/**
 * TrackIdGenerator
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-09 15:49:40
 */
public class DefaultIdGenerator implements IdGenerator {

    private static final DefaultIdGenerator instance = new DefaultIdGenerator();

    private DefaultIdGenerator() {
        super();
    }

    public static DefaultIdGenerator getInstance() {
        return instance;
    }

    @Override
    public String generate() {
        return UUID.randomUUID().toString().toLowerCase(Locale.ROOT);
    }
}
