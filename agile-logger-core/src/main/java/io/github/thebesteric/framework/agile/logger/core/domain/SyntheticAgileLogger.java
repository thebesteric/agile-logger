package io.github.thebesteric.framework.agile.logger.core.domain;

import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * <p>Merge {@link AgileLogger} on Class and {@link AgileLogger} on Method.
 * When the same attribute exists on both class and method, the attribute of method takes effect
 *
 * @author Eric Joe
 * @version 1.0
 * @see io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger
 * @since 2022-08-05 23:45:22
 */
public class SyntheticAgileLogger {

    protected Method method;
    protected String tag;
    protected String extra;
    protected String level;
    protected String exception;
    protected String[] ignoreMethods;
    protected boolean matched = true;

    public SyntheticAgileLogger(Method method) {
        this(method, AbstractEntity.TAG_DEFAULT, AbstractEntity.LEVEL_INFO);
    }

    public SyntheticAgileLogger(Method method, String defaultTag) {
        this(method, defaultTag, AbstractEntity.LEVEL_INFO);
    }

    public SyntheticAgileLogger(Method method, String defaultTag, String defaultLevel) {
        AgileLogger onType = method.getDeclaringClass().getAnnotation(AgileLogger.class);
        AgileLogger onMethod = method.getAnnotation(AgileLogger.class);

        String tagOnType = null, tagOnMethod = null;
        String extraOnType = null, extraOnMethod = null;
        String levelOnType = null, levelOnMethod = null;
        List<String> ignoreMethodsOnType = null, ignoreMethodsOnMethod = null, mergedIgnoreMethods = new ArrayList<>();

        if (onType != null) {
            tagOnType = onType.tag();
            extraOnType = onType.extra();
            levelOnType = onType.level();
            ignoreMethodsOnType = List.of(onType.ignoreMethods());
        }
        if (onMethod != null) {
            tagOnMethod = onMethod.tag();
            extraOnMethod = onMethod.extra();
            levelOnMethod = onMethod.level();
            ignoreMethodsOnMethod = List.of(onMethod.ignoreMethods());
        }

        // Merge ignore methods
        if (ignoreMethodsOnType != null) {
            mergedIgnoreMethods.addAll(ignoreMethodsOnType);
        }
        if (ignoreMethodsOnMethod != null) {
            mergedIgnoreMethods.addAll(ignoreMethodsOnMethod);
        }

        this.method = method;
        this.tag = StringUtils.blankToNull(StringUtils.notEquals(defaultTag, tagOnMethod) ?
                (tagOnMethod != null ? tagOnMethod : tagOnType) : (tagOnType == null ? defaultTag : tagOnType));
        this.level = StringUtils.blankToNull(StringUtils.notEquals(defaultLevel, levelOnMethod) ?
                (levelOnMethod != null ? levelOnMethod : levelOnType) : (levelOnType == null ? defaultLevel : levelOnType));
        this.extra = StringUtils.blankToNull(StringUtils.isNotEmpty(extraOnMethod) ? extraOnMethod : extraOnType);
        this.ignoreMethods = new HashSet<>(mergedIgnoreMethods).toArray(new String[0]);

        // Has not annotated @AgileLogger
        if (onType == null && onMethod == null) {
            this.level = defaultLevel;
            this.tag = defaultTag;
            this.matched = false;
        }
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = StringUtils.blankToNull(tag);
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = StringUtils.blankToNull(extra);
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = StringUtils.blankToNull(level);
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String[] getIgnoreMethods() {
        return ignoreMethods;
    }

    public void setIgnoreMethods(String[] ignoreMethods) {
        this.ignoreMethods = ignoreMethods;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }
}
