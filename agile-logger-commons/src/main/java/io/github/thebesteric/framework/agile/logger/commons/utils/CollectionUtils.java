package io.github.thebesteric.framework.agile.logger.commons.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CollectionUtils
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-05 18:03:57
 */
public class CollectionUtils {

    public static boolean isEmpty(Object[] arr) {
        return ArrayUtils.isEmpty(arr);
    }

    public static boolean isEmpty(boolean[] arr) {
        return ArrayUtils.isEmpty(arr);
    }

    public static boolean isEmpty(char[] arr) {
        return ArrayUtils.isEmpty(arr);
    }

    public static boolean isEmpty(byte[] arr) {
        return ArrayUtils.isEmpty(arr);
    }

    public static boolean isEmpty(short[] arr) {
        return ArrayUtils.isEmpty(arr);
    }

    public static boolean isEmpty(int[] arr) {
        return ArrayUtils.isEmpty(arr);
    }

    public static boolean isEmpty(long[] arr) {
        return ArrayUtils.isEmpty(arr);
    }

    public static boolean isEmpty(float[] arr) {
        return ArrayUtils.isEmpty(arr);
    }

    public static boolean isEmpty(double[] arr) {
        return ArrayUtils.isEmpty(arr);
    }

    public static boolean isNotEmpty(Object[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(boolean[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(char[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(byte[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(short[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(int[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(long[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(float[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(double[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isEmpty(Collection<?> collection) {
        if (collection == null) return true;
        return org.apache.commons.collections4.CollectionUtils.isEmpty(collection);
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    @SafeVarargs
    public static <T> List<T> createList(T... items) {
        if (items == null || items.length == 0) return new ArrayList<>();
        return Stream.of(items).collect(Collectors.toList());
    }

    @SafeVarargs
    public static <T> Set<T> createSet(T... items) {
        if (items == null || items.length == 0) return new HashSet<>();
        return Stream.of(items).collect(Collectors.toSet());
    }

}
