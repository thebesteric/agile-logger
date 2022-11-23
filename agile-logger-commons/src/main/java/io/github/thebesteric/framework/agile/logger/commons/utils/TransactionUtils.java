package io.github.thebesteric.framework.agile.logger.commons.utils;

import java.util.List;
import java.util.UUID;

/**
 * TransactionUtils
 *
 * @author Eric Joe
 * @since 1.0
 */
public class TransactionUtils {

    public static List<String> TRACK_ID_NAMES = CollectionUtils.createList("track-id", "x-track-id", "trans-id", "x-trans-id", "trace-id", "x-trace-id", "transaction-id", "x-transaction-id");

    private static ThreadLocal<String> trackIdThreadLocal = TransactionUtils.create();

    public static ThreadLocal<String> create() {
        return create(UUID.randomUUID().toString());
    }

    public static ThreadLocal<String> create(String trackId) {
        if (trackIdThreadLocal != null) {
            trackIdThreadLocal.remove();
        }
        return ThreadLocal.withInitial(() -> trackId);
    }

    public static String get() {
        return trackIdThreadLocal.get();
    }

    public static void set(String trackId) {
        trackIdThreadLocal.set(trackId);
    }

    public static void initialize() {
        trackIdThreadLocal = create();
    }

    public static void initialize(String trackId) {
        trackIdThreadLocal = create(trackId);
    }

    public static boolean hasTrackId(String header) {
        for (String name : TRACK_ID_NAMES) {
            if (header.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
