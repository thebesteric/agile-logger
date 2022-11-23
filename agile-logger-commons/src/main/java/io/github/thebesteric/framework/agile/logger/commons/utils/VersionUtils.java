package io.github.thebesteric.framework.agile.logger.commons.utils;

import java.util.List;

/**
 * VersionUtils
 *
 * @author Eric Joe
 * @version 1.0
 */
public class VersionUtils {

    public static List<String> VERSION_NAMES = CollectionUtils.createList("version", "x-version", "app-version", "x-app-version");
    private static final ThreadLocal<String> versionThreadLocal = new ThreadLocal<>();

    public static String get() {
        return versionThreadLocal.get();
    }

    public static void set(String version) {
        versionThreadLocal.set(version);
    }

    public static int compare(int appVersion, int compareVersion) {
        return Integer.compare(appVersion, compareVersion);
    }

    public static boolean compareEqual(int appVersion, int compareVersion) {
        return appVersion == compareVersion;
    }

    public static boolean compareGreaterThan(int appVersion, int compareVersion) {
        return appVersion > compareVersion;
    }

    public static boolean compareGreaterThanOrEqual(int appVersion, int compareVersion) {
        return appVersion >= compareVersion;
    }

    public static boolean compareLessThan(int appVersion, int compareVersion) {
        return appVersion < compareVersion;
    }

    public static boolean compareLessThanOrEqual(int appVersion, int compareVersion) {
        return appVersion <= compareVersion;
    }

    public static int compare(String appVersion, String compareVersion) {
        return compare(appVersion, compareVersion, 0);
    }

    public static int compare(String appVersion, String compareVersion, int digits) {

        Integer result = versionEmptyCheck(appVersion, compareVersion);
        if (result != null) {
            return result;
        }

        String[] array1 = appVersion.split("\\.");
        String[] array2 = compareVersion.split("\\.");
        int len1 = array1.length;
        int len2 = array2.length;
        int compareLength = Math.min(len1, len2);
        try {
            result = toCompare(array1, array2, 0, compareLength, digits);
            if (result == 0 && len1 > len2 && digits <= 0) {
                return 1;
            } else if (result == 0 && len1 < len2 && digits <= 0) {
                return -1;
            } else {
                return result;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad version");
        }
    }

    public static boolean compareEqual(String appVersion, String compareVersion) {
        return compareEqual(appVersion, compareVersion, 0);
    }

    public static boolean compareEqual(String appVersion, String compareVersion, int digits) {
        return compare(appVersion, compareVersion, digits) == 0;
    }

    public static boolean compareGreaterThan(String appVersion, String compareVersion) {
        return compareGreaterThan(appVersion, compareVersion, 0);
    }

    public static boolean compareGreaterThan(String appVersion, String compareVersion, int digits) {
        return compare(appVersion, compareVersion, digits) > 0;
    }

    public static boolean compareGreaterThanOrEqual(String appVersion, String compareVersion) {
        return compareGreaterThanOrEqual(appVersion, compareVersion, 0);
    }

    public static boolean compareGreaterThanOrEqual(String appVersion, String compareVersion, int digits) {
        return compare(appVersion, compareVersion, digits) >= 0;
    }

    public static boolean compareLessThan(String appVersion, String compareVersion) {
        return compareLessThan(appVersion, compareVersion, 0);
    }

    public static boolean compareLessThan(String appVersion, String compareVersion, int digits) {
        return compare(appVersion, compareVersion, digits) < 0;
    }

    public static boolean compareLessThanOrEqual(String appVersion, String compareVersion) {
        return compareLessThanOrEqual(appVersion, compareVersion, 0);
    }

    public static boolean compareLessThanOrEqual(String appVersion, String compareVersion, int digits) {
        return compare(appVersion, compareVersion, digits) <= 0;
    }

    private static Integer versionEmptyCheck(String appVersion, String compareVersion) {
        if (StringUtils.isEmpty(appVersion) && StringUtils.isNotEmpty(compareVersion)) {
            return -1;
        } else if (StringUtils.isNotEmpty(appVersion) && StringUtils.isEmpty(compareVersion)) {
            return 1;
        }
        return null;
    }

    private static int toCompare(String[] appVersionArr, String[] compareVersionArr, int begin, int end, int digits) {
        try {
            if (begin == end) {
                return 0;
            }
            int v1 = Integer.parseInt(appVersionArr[begin]);
            int v2 = Integer.parseInt(compareVersionArr[begin]);
            if (v1 > v2) {
                return 1;
            } else if (v1 < v2) {
                return -1;
            } else {
                if (digits > 0 && --digits == 0) {
                    return 0;
                }
                return toCompare(appVersionArr, compareVersionArr, ++begin, end, digits);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad version");
        }
    }

    public static boolean hasVersion(String header) {
        for (String name : VERSION_NAMES) {
            if (header.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
