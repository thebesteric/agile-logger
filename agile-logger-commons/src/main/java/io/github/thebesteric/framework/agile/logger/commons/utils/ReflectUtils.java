package io.github.thebesteric.framework.agile.logger.commons.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * JsonUtils
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-27 23:05:15
 */
public class ReflectUtils {

    public static boolean isPublic(Class<?> clazz) {
        return Modifier.isPublic(clazz.getModifiers());
    }

    public static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    public static boolean isPrivate(Class<?> clazz) {
        return Modifier.isPrivate(clazz.getModifiers());
    }

    public static boolean isPrivate(Member member) {
        return Modifier.isPrivate(member.getModifiers());
    }

    public static boolean isProtected(Class<?> clazz) {
        return Modifier.isProtected(clazz.getModifiers());
    }

    public static boolean isProtected(Member member) {
        return Modifier.isProtected(member.getModifiers());
    }

    public static boolean isStatic(Class<?> clazz) {
        return Modifier.isStatic(clazz.getModifiers());
    }

    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    public static boolean isFinal(Class<?> clazz) {
        return Modifier.isFinal(clazz.getModifiers());
    }

    public static boolean isFinal(Member member) {
        return Modifier.isFinal(member.getModifiers());
    }

    public static boolean isPrimitive(Field field) {
        return isPrimitive(field.getType());
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getDefaultValue(Class<T> clazz) {
        return (T) Array.get(Array.newInstance(clazz, 1), 0);
    }

    public static Object getDefaultValue(Field field) {
        return getDefaultValue(field.getType());
    }

    public static String[] getModifiers(Class<?> clazz) {
        return Modifier.toString(clazz.getModifiers()).split(" ");
    }

    public static String[] getModifiers(Member member) {
        return Modifier.toString(member.getModifiers()).split(" ");
    }

    public static List<Field> getFields(Class<?> clazz, Predicate<Field> predicate) {
        List<Field> fields = new ArrayList<>();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (predicate == null || predicate.test(field)) {
                    fields.add(field);
                }
            }
        }
        return fields;
    }

    public static List<Field> getFields(Class<?> clazz) {
        return getFields(clazz, null);
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        return getFields(clazz, null).stream().filter(field -> fieldName.equals(field.getName())).findFirst().orElse(null);
    }

    public static List<String> getFieldNames(Class<?> clazz, Predicate<Field> predicate) {
        List<Field> fields = getFields(clazz, predicate);
        List<String> fieldNames = new ArrayList<>();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    public static List<String> getFieldNames(Class<?> clazz) {
        return getFieldNames(clazz, null);
    }

    public static <T extends Annotation> T getAnnotation(Class<?> objectClass, Class<T> annotationClass) {
        return objectClass.getAnnotation(annotationClass);
    }

    public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    public static <T extends Annotation> T getAnnotation(Field field, Class<T> annotationClass) {
        return field.getAnnotation(annotationClass);
    }

    public static boolean anyAnnotationPresent(Class<?> objectClass, Class<? extends Annotation> annotationClass) {
        if (isAnnotationPresent(objectClass, annotationClass)) {
            return true;
        } else {
            for (Method method : objectClass.getDeclaredMethods()) {
                if (isAnnotationPresent(method, annotationClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    @SafeVarargs
    public static boolean isAnnotationPresent(Class<?> objectClass, Class<? extends Annotation> annotationClass, Class<? extends Annotation>... annotationClasses) {
        List<Class<? extends Annotation>> annoClasses = mergeIndefiniteParams(annotationClass, annotationClasses);
        return annoClasses.stream().allMatch(objectClass::isAnnotationPresent);
    }

    @SafeVarargs
    public static boolean isAnnotationPresent(Method method, Class<? extends Annotation> annotationClass, Class<? extends Annotation>... annotationClasses) {
        List<Class<? extends Annotation>> annoClasses = mergeIndefiniteParams(annotationClass, annotationClasses);
        return annoClasses.stream().allMatch(method::isAnnotationPresent);
    }

    @SafeVarargs
    private static <T> List<T> mergeIndefiniteParams(T t, T... IndefiniteParams) {
        List<T> list = new ArrayList<>();
        list.add(t);
        if (IndefiniteParams != null && IndefiniteParams.length > 0) {
            list.addAll(Arrays.asList(IndefiniteParams));
        }
        return list;
    }

    public static void set(Field field, Object target, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(target, field.get(value));
    }

    public static Class<?> getClassByMethod(Method method) {
        return method.getDeclaringClass();
    }

    public static List<Method> getMethods(Class<?> clazz) {
        return Arrays.asList(clazz.getMethods());
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) throws NoSuchMethodException {
        return clazz.getDeclaredMethod(methodName, parameterTypes);
    }

    public static Constructor<?> determineConstructor(Class<?> clazz) {
        Constructor<?>[] rawCandidates = clazz.getDeclaredConstructors();
        List<Constructor<?>> constructors = Arrays.asList(rawCandidates);
        constructors.sort((o1, o2) -> {
            if (o1.getParameterCount() != o2.getParameterCount()) {
                return o1.getParameterCount() > o2.getParameterCount() ? 1 : -1;
            }
            return 0;
        });
        return constructors.get(0);
    }

    public static Object newInstance(Constructor<?> constructor) throws Throwable {
        constructor.setAccessible(true);
        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = ObjectUtils.initialValue(parameters[i].getType());
        }
        return constructor.newInstance(args);
    }


}
