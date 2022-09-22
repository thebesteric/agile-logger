package io.github.thebesteric.framework.agile.logger.spring.processor;

import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IgnoreMethodProcessor
 *
 * @author Eric Joe
 * @version 1.0
 */
public interface IgnoreMethodProcessor {
    Set<IgnoreMethod> IGNORE_METHODS = new HashSet<>(16);

    /**
     * Add ignore uris
     *
     * @param ignoreMethods ignoreMethods
     * @see IgnoreMethod
     */
    void add(Set<IgnoreMethod> ignoreMethods);

    /**
     * Get IGNORE_URLS
     *
     * @return {@link Set<IgnoreMethod>}
     * @see IgnoreMethod
     */
    default Set<IgnoreMethod> get() {
        return IGNORE_METHODS;
    }

    /**
     * Whether the value matching the regular
     *
     * @param method {@link IgnoreMethod}
     * @return boolean
     * @see IgnoreMethod
     */
    default boolean matching(IgnoreMethod method) {
        for (IgnoreMethod ignoreMethod : IGNORE_METHODS) {
            Matcher classNameMatcher = Pattern.compile(ignoreMethod.className).matcher(method.className);
            Matcher methodNameMatcher = Pattern.compile(ignoreMethod.methodName).matcher(method.methodName);
            if (classNameMatcher.find() && methodNameMatcher.find()) {
                return true;
            }
        }
        return false;
    }

    default void addDefaultIgnoreMethods() {
        IGNORE_METHODS.add(IgnoreMethod.builder().methodName("toString").build());
        IGNORE_METHODS.add(IgnoreMethod.builder().methodName("hashCode").build());
        IGNORE_METHODS.add(IgnoreMethod.builder().methodName("equals").build());
    }

    @Getter
    @Setter
    class IgnoreMethod {
        private String className;
        private String methodName;

        private IgnoreMethod() {
            super();
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private final IgnoreMethod ignoreMethod;

            public Builder() {
                ignoreMethod = new IgnoreMethod();
            }

            public Builder className(String className) {
                ignoreMethod.className = className;
                return this;
            }

            public Builder methodName(String methodName) {
                ignoreMethod.methodName = methodName;
                return this;
            }

            public Builder clazz(Class<?> clazz) {
                return className(clazz.getName());
            }

            public Builder method(Method method) {
                return methodName(method.getName());
            }


            public IgnoreMethod build() {
                if (StringUtils.isEmpty(ignoreMethod.className)) {
                    ignoreMethod.className = ".*";
                }
                if (StringUtils.isEmpty(ignoreMethod.methodName)) {
                    throw new IllegalArgumentException("methodName cannot be empty");
                }
                return ignoreMethod;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IgnoreMethod that = (IgnoreMethod) o;
            return new EqualsBuilder().append(className, that.className).append(methodName, that.methodName).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(className).append(methodName).toHashCode();
        }

        @Override
        public String toString() {
            return this.className + "#" + this.methodName;
        }
    }
}
