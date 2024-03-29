package io.github.thebesteric.framework.agile.logger.commons.utils;

import io.github.thebesteric.framework.agile.logger.commons.exception.ClassNotFoundException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JsonUtils
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-07-27 23:05:15
 */
public class ReflectUtils {

    public static void setAccessible(Method method, boolean accessible) {
        method.setAccessible(accessible);
    }

    public static void setAccessible(Method method) {
        setAccessible(method, true);
    }

    public static void setAccessible(Field field, boolean accessible) {
        field.setAccessible(accessible);
    }

    public static void setAccessible(Field field) {
        setAccessible(field, true);
    }

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

    public static boolean isTransient(Class<?> clazz) {
        return Modifier.isTransient(clazz.getModifiers());
    }

    public static boolean isTransient(Member member) {
        return Modifier.isTransient(member.getModifiers());
    }

    public static boolean isPrimitive(Field field) {
        return isPrimitive(field.getType());
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive();
    }

    public static boolean isPrimitiveOrWarp(Field field) {
        return isPrimitiveOrWarp(field.getType());
    }

    public static boolean isPrimitiveOrWarp(Class<?> clazz) {
        try {
            return clazz.isPrimitive() || ((Class<?>) clazz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception ignore) {
            return false;
        }
    }

    public static boolean isListType(Class<?> clazz) {
        return List.class.isAssignableFrom(clazz);
    }

    public static boolean isListType(Field field) {
        Class<?> type = field.getType();
        return isListType(type);
    }

    public static boolean isMapType(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    public static boolean isMapType(Field field) {
        Class<?> type = field.getType();
        return isMapType(type);
    }

    public static boolean isArrayType(Class<?> clazz) {
        return clazz.isArray();
    }

    public static boolean isArrayType(Field field) {
        Class<?> type = field.getType();
        return isArrayType(type);
    }

    public static boolean isStringType(Class<?> clazz) {
        return String.class == clazz;
    }

    public static boolean isStringType(Field field) {
        Class<?> type = field.getType();
        return isStringType(type);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getDefaultValue(Class<T> clazz) {
        return (T) Array.get(Array.newInstance(clazz, 1), 0);
    }

    public static Object getDefaultValue(Field field) {
        return getDefaultValue(field.getType());
    }

    public static Object parsePrimitiveOrWarpByType(String value, Class<?> clazz) {
        Object result = value;
        if (clazz == char.class || clazz == Character.class) {
            result = value.charAt(0);
        } else if (clazz == byte.class || clazz == Byte.class) {
            result = Byte.parseByte(value);
        } else if (clazz == short.class || clazz == Short.class) {
            result = Short.parseShort(value);
        } else if (clazz == int.class || clazz == Integer.class) {
            result = Integer.parseInt(value);
        } else if (clazz == long.class || clazz == Long.class) {
            result = Long.parseLong(value);
        } else if (clazz == float.class || clazz == Float.class) {
            result = Float.parseFloat(value);
        } else if (clazz == double.class || clazz == Double.class) {
            result = Double.parseDouble(value);
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            result = Boolean.parseBoolean(value);
        }
        return result;
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

    public static boolean isAnnotationPresent(Class<?> objectClass, Class<? extends Annotation> annotationClass) {
        return isAnnotationPresent(objectClass, annotationClass, false);
    }

    public static boolean isAnnotationPresent(Class<?> objectClass, Class<? extends Annotation> annotationClass, boolean typeAndMethod) {
        if (allAnnotationPresent(objectClass, annotationClass)) {
            return true;
        } else if (typeAndMethod) {
            for (Method method : objectClass.getDeclaredMethods()) {
                if (allAnnotationPresent(method, annotationClass)) {
                    return true;
                }
            }
        }

        return false;
    }

    @SafeVarargs
    public static boolean anyAnnotationPresent(Class<?> objectClass, Class<? extends Annotation> annotationClass, Class<? extends Annotation>... annotationClasses) {
        List<Class<? extends Annotation>> annoClasses = mergeIndefiniteParams(annotationClass, annotationClasses);
        return annoClasses.stream().anyMatch(objectClass::isAnnotationPresent);
    }

    @SafeVarargs
    public static boolean anyAnnotationPresent(Method method, Class<? extends Annotation> annotationClass, Class<? extends Annotation>... annotationClasses) {
        List<Class<? extends Annotation>> annoClasses = mergeIndefiniteParams(annotationClass, annotationClasses);
        return annoClasses.stream().anyMatch(method::isAnnotationPresent);
    }

    @SafeVarargs
    public static boolean allAnnotationPresent(Class<?> objectClass, Class<? extends Annotation> annotationClass, Class<? extends Annotation>... annotationClasses) {
        List<Class<? extends Annotation>> annoClasses = mergeIndefiniteParams(annotationClass, annotationClasses);
        return annoClasses.stream().allMatch(objectClass::isAnnotationPresent);
    }

    @SafeVarargs
    public static boolean allAnnotationPresent(Method method, Class<? extends Annotation> annotationClass, Class<? extends Annotation>... annotationClasses) {
        List<Class<? extends Annotation>> annoClasses = mergeIndefiniteParams(annotationClass, annotationClasses);
        return annoClasses.stream().allMatch(method::isAnnotationPresent);
    }

    public static List<Field> getAnnotationFields(Class<? extends Annotation> annotationClass, Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClazz = clazz;
        do {
            Field[] declaredFields = currentClazz.getDeclaredFields();
            for (Field field : declaredFields) {
                if (field.isAnnotationPresent(annotationClass)) {
                    fields.add(field);
                }
            }
            currentClazz = currentClazz.getSuperclass();
        } while (currentClazz != null && currentClazz != Object.class);
        return fields;
    }

    public static Map<Field, Object> getAnnotationFields(Class<? extends Annotation> annotationClass, Object object) throws IllegalAccessException {
        Map<Field, Object> annotationFields = new LinkedHashMap<>();
        Class<?> clazz = object.getClass();
        Class<?> currentClazz = clazz;
        do {
            if (currentClazz == AbstractList.class) {
                break;
            }
            Field[] declaredFields = currentClazz.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(annotationClass) && !isTransient(field)) {
                    annotationFields.put(field, field.get(object));
                } else {
                    if (!isPrimitiveOrWarp(field) && !isStringType(field) && !isFinal(field)) {
                        // List type
                        if (isListType(field)) {
                            List<?> list = (List<?>) field.get(object);
                            if (list != null) {
                                for (Object obj : list) {
                                    if (obj != null && !ReflectUtils.isPrimitiveOrWarp(obj.getClass()) && !ReflectUtils.isStringType(obj.getClass())) {
                                        getAnnotationFields(annotationClass, obj);
                                    }
                                }
                            }
                        }
                        // Array type
                        else if (isArrayType(field)) {
                            Object[] arr = (Object[]) field.get(object);
                            if (arr != null) {
                                for (Object obj : arr) {
                                    if (obj != null && !ReflectUtils.isPrimitiveOrWarp(obj.getClass()) && !ReflectUtils.isStringType(obj.getClass())) {
                                        getAnnotationFields(annotationClass, obj);
                                    }
                                }
                            }
                        }
                        // Map type
                        else if (isMapType(field)) {
                            Map<?, ?> map = (Map<?, ?>) field.get(object);
                            if (map != null) {
                                for (Map.Entry<?, ?> entry : map.entrySet()) {
                                    Object key = entry.getKey();
                                    Object value = entry.getValue();
                                    if (!ReflectUtils.isPrimitiveOrWarp(key.getClass()) && !ReflectUtils.isStringType(key.getClass())) {
                                        getAnnotationFields(annotationClass, key);
                                    }
                                    if (!ReflectUtils.isPrimitiveOrWarp(value.getClass())) {
                                        getAnnotationFields(annotationClass, value);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            currentClazz = clazz.getSuperclass();
        } while (currentClazz != null && currentClazz != Object.class);
        return annotationFields;
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

    public static List<Class<?>> getActualTypeArguments(Class<?> clazz, Class<?> actualTypeClass) throws ClassNotFoundException {
        Type genericClass = getSuperclassOnType(clazz, actualTypeClass);
        if (genericClass == Object.class) {
            genericClass = getInterfaceClassOnType(clazz, actualTypeClass);
            if (genericClass == null) {
                throw new ClassNotFoundException("Can not found class: %s", actualTypeClass.getName());
            }
        }

        ParameterizedType parameterizedType;
        try {
            if (genericClass != null) {
                parameterizedType = (ParameterizedType) genericClass;
            } else {
                parameterizedType = (ParameterizedType) getInterfaceClassOnType(clazz, actualTypeClass);
            }
        } catch (Exception ex) {
            if (genericClass instanceof Class<?>) {
                parameterizedType = null;
            } else {
                parameterizedType = (ParameterizedType) getInterfaceClassOnType(clazz, actualTypeClass);
            }
        }

        List<Type> actualTypeArguments;
        if (parameterizedType == null) {
            actualTypeArguments = CollectionUtils.createList(Object.class);
        } else {
            actualTypeArguments = Arrays.asList(parameterizedType.getActualTypeArguments());
        }
        return actualTypeArguments.stream().flatMap(type -> {
            Class<?> typeClass;
            if (type instanceof ParameterizedType) {
                typeClass = (Class<?>) ((ParameterizedType) type).getRawType();
            } else {
                typeClass = (Class<?>) type;
            }
            return Stream.of(typeClass);
        }).collect(Collectors.toList());
    }

    public static Type getSuperclassOnType(Class<?> type, Class<?> clazz) {
        Class<?> superClass;
        Type genericSuperclass = type.getGenericSuperclass();
        if (genericSuperclass instanceof Class) {
            superClass = (Class<?>) genericSuperclass;
        } else {
            superClass = (Class<?>) ((ParameterizedType) genericSuperclass).getRawType();
        }
        do {
            if (clazz.isAssignableFrom(superClass)) {
                return genericSuperclass;
            }
            Type targetType = getInterfaceClassOnType(superClass, clazz);
            if (targetType != null && ((ParameterizedType) targetType).getRawType() == clazz) {
                return targetType;
            }
            superClass = (Class<?>) superClass.getGenericSuperclass();
        } while (superClass != Object.class && superClass != null);
        return null;
    }

    public static Type getInterfaceClassOnType(Class<?> type, Class<?> interfaceClazz) {
        return Arrays.stream(type.getGenericInterfaces())
                .filter(genericInterface -> ((ParameterizedType) genericInterface).getRawType() == interfaceClazz)
                .findFirst().orElse(null);
    }

    public static Class<?> getListActualTypeArgument(Field field) {
        if (isListType(field)) {
            Type[] actualTypeArguments = getActualTypeArguments(field);
            if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                return (Class<?>) actualTypeArguments[0];
            }
        }
        throw new IllegalStateException("field is not a List type");
    }

    public static Class<?>[] getMapActualTypeArgument(Field field) {
        if (isListType(field)) {
            Type[] actualTypeArguments = getActualTypeArguments(field);
            if (actualTypeArguments != null && actualTypeArguments.length == 2) {
                return new Class<?>[]{(Class<?>) actualTypeArguments[0], (Class<?>) actualTypeArguments[1]};
            }
        }
        throw new IllegalStateException("field is not a Map type");
    }

    public static Type[] getActualTypeArguments(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            return pt.getActualTypeArguments();
        }
        return null;
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

    public static Class<?> getClassForName(String className) {
        Class<?> aClass = null;
        try {
            aClass = Class.forName(className);
        } catch (java.lang.ClassNotFoundException ignored) {
        }
        return aClass;
    }

}
