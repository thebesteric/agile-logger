package io.github.thebesteric.framework.agile.logger.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateUtils
 *
 * @author Eric Joe
 * @version 1.0
 */
public class DateUtils {

    public static String format(Date date, SimpleDateFormat format) {
        return format.format(date);
    }

    public static String format(Date date, String format) {
        return format(date, new SimpleDateFormat(format));
    }

}
