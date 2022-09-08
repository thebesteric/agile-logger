package io.github.thebesteric.framework.agile.logger.commons.utils;

/**
 * ExceptionUtils
 *
 * @author Eric Joe
 * @since 1.0
 */
public class ExceptionUtils {

    public static String getSimpleMessage(Throwable ex, int limit) {
        return StringUtils.limit(getSimpleMessage(ex), limit);
    }

    public static String getSimpleMessage(Throwable ex) {
        if (ex != null) {
            String exTitle = getTitle(ex);
            StackTraceElement exCause = getMajorCause(ex);
            return exTitle + (exCause == null ? "" : ": " + exCause);
        }
        return null;
    }

    public static String getTitle(Throwable ex) {
        return StringUtils.isNotEmpty(ex.getMessage()) ? ex.getMessage() : ex.toString();
    }

    public static StackTraceElement[] getCauses(Throwable ex) {
        return ex.getStackTrace();
    }

    public static StackTraceElement getMajorCause(Throwable ex) {
        StackTraceElement[] causes = getCauses(ex);
        return CollectionUtils.isEmpty(causes) ? null : causes[0];
    }

}
