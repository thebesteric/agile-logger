package io.github.thebesteric.framework.agile.logger.commons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JsonUtils
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-27 23:06:28
 */
public class ObjectUtils {

    private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");

    public static String humpToUnderline(String str) {
        if (Character.isUpperCase(str.charAt(0))) {
            str = toLowerCaseFirst(str);
        }

        Matcher matcher = HUMP_PATTERN.matcher(str);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String toLowerCaseFirst(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static String toUpperCaseFirst(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static boolean isEmpty(Object object) {
        return org.apache.commons.lang3.ObjectUtils.isEmpty(object);
    }

    public static boolean isNotEmpty(Object object) {
        return org.apache.commons.lang3.ObjectUtils.isNotEmpty(object);
    }

    public static boolean allNotNull(Object... objs) {
        return org.apache.commons.lang3.ObjectUtils.allNotNull(objs);
    }

    public static boolean allNull(Object... objs) {
        return org.apache.commons.lang3.ObjectUtils.allNull(objs);
    }

    public static boolean anyNotNull(Object... objs) {
        return org.apache.commons.lang3.ObjectUtils.anyNotNull(objs);
    }

    public static boolean anyNull(Object... objs) {
        return org.apache.commons.lang3.ObjectUtils.anyNull(objs);
    }

    public static <T> T requireNonNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }

    public static Object initialValue(Class<?> clazz) {
        Object object = null;
        if (clazz.isPrimitive()) {
            if (clazz == char.class) {
                object = '\u0000';
            } else if (clazz == byte.class) {
                object = 0;
            } else if (clazz == short.class) {
                object = 0;
            } else if (clazz == int.class) {
                object = 0;
            } else if (clazz == long.class) {
                object = 0L;
            } else if (clazz == float.class) {
                object = 0.0F;
            } else if (clazz == double.class) {
                object = 0.0D;
            } else if (clazz == boolean.class) {
                object = false;
            }
        }
        return object;
    }

}
