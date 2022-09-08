package io.github.thebesteric.framework.agile.logger.commons.utils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * StringUtils
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-06 00:06:10
 */
public class StringUtils {

    public static boolean isEmpty(String str) {
        return org.apache.commons.lang3.StringUtils.isEmpty(str) || Objects.equals(str, "\"\"");
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEquals(String str1, String str2) {
        str1 = nullToBlank(str1);
        str2 = nullToBlank(str2);
        return str1.equals(str2);
    }

    public static boolean isNotEquals(String str1, String str2) {
        return !isEquals(str1, str2);
    }

    public static boolean isEqualsIgnoreCase(String str1, String str2) {
        str1 = nullToBlank(str1);
        str2 = nullToBlank(str2);
        return str1.equalsIgnoreCase(str2);
    }


    public static boolean isNotEqualsIgnoreCase(String str1, String str2) {
        return !isEqualsIgnoreCase(str1, str2);
    }

    public static String nullToBlank(String str) {
        return str == null ? "" : str;
    }

    public static String blankToNull(String cs) {
        return isEmpty(cs) ? null : cs;
    }

    public static String toLowerFirst(String str) {
        if (isNotEmpty(str)) {
            char[] cs = str.toCharArray();
            if (cs[0] >= 54 && cs[0] <= 90) {
                cs[0] += 32;
                return String.valueOf(cs);
            }
        }
        return str;
    }

    public static String toUpperFirst(String str) {
        if (isNotEmpty(str)) {
            char[] cs = str.toCharArray();
            if (cs[0] >= 97 && cs[0] <= 122) {
                cs[0] -= 32;
                return String.valueOf(cs);
            }
        }
        return str;
    }

    public static String bytesToString(byte[] bytes) {
        return CollectionUtils.isNotEmpty(bytes) ? new String(bytes, StandardCharsets.UTF_8) : null;
    }

    public static String limit(String str, int limit) {
        if (str != null && str.length() > limit) {
            str = str.substring(0, limit);
        }
        return str;
    }
}
