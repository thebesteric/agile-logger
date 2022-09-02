package io.github.thebesteric.framework.agile.logger.core.domain;

import io.github.thebesteric.framework.agile.logger.commons.utils.StringUtils;
import io.github.thebesteric.framework.agile.logger.core.annotation.AgileLogger;

import java.lang.reflect.Method;

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

    private String prefix;
    private String tag;
    private String extra;
    private String level;
    private String[] ignoreMethods;

    public SyntheticAgileLogger(Method method) {
        this(method.getDeclaringClass().getAnnotation(AgileLogger.class), method.getAnnotation(AgileLogger.class));
    }

    public SyntheticAgileLogger(AgileLogger onType, AgileLogger onMethod) {
        String tagOnType = null, tagOnMethod = null;
        String extraOnType = null, extraOnMethod = null;
        String levelOnType = null, levelOnMethod = null;
        String[] ignoreMethodsOnType = null;
        if (onType != null) {
            tagOnType = onType.tag();
            extraOnType = onType.extra();
            levelOnType = onType.level();
            ignoreMethodsOnType = onType.ignoreMethods();
        }
        if (onMethod != null) {
            tagOnMethod = onMethod.tag();
            extraOnMethod = onMethod.extra();
            levelOnMethod = onMethod.level();
        }
        this.extra = StringUtils.isNotEmpty(extraOnMethod) ? extraOnMethod : extraOnType;
        this.tag = StringUtils.isNotEquals(AbstractEntity.TAG_DEFAULT, tagOnMethod) ? tagOnMethod != null ? tagOnMethod : tagOnType : tagOnType;
        this.level = StringUtils.isNotEquals(AbstractEntity.LEVEL_INFO, levelOnMethod) ? levelOnMethod != null ? levelOnMethod : levelOnType : levelOnType;
        this.ignoreMethods = ignoreMethodsOnType;

        // If Controller has not @AgileLogger
        if (onType == null && onMethod == null) {
            this.level = AbstractEntity.LEVEL_INFO;
            this.tag = AbstractEntity.TAG_DEFAULT;
        }

    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String[] getIgnoreMethods() {
        return ignoreMethods;
    }

    public void setIgnoreMethods(String[] ignoreMethods) {
        this.ignoreMethods = ignoreMethods;
    }
}
